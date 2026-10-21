package com.luc4n3x.levyra.player

import com.luc4n3x.levyra.domain.LevyraAudioSettings
import com.luc4n3x.levyra.domain.RepeatMode
import com.luc4n3x.levyra.domain.Track
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class AutoMixPlan(val transitionMs: Long, val preloadLeadMs: Long)
data class CrossfadeGains(val outgoing: Float, val incoming: Float)

internal fun planAutoMix(
    current: Track,
    next: Track?,
    settings: LevyraAudioSettings,
    repeatMode: RepeatMode,
    videoMode: Boolean,
    lowRam: Boolean,
    shuffleEnabled: Boolean = false,
    djEnabled: Boolean = false
): AutoMixPlan? {
    val normalized = settings.normalized()
    val automaticDjMix = (djEnabled || normalized.djSoftMode) && normalized.crossfadeSeconds == 0
    val effectiveSeconds = if (automaticDjMix) 27 else normalized.crossfadeSeconds
    if (!normalized.gaplessEnabled || effectiveSeconds <= 0 || next == null) return null
    if (repeatMode == RepeatMode.One || videoMode || lowRam || current.durationMs <= MIN_TRACK_MS) return null
    if (continuesSameRelease(current, next, shuffleEnabled)) return null
    val base = effectiveSeconds * 1_000L
    val adaptive = if (automaticDjMix) {
        // A fixed 27-second overlay hides neither mismatched beats nor competing vocals.
        // Unknown metadata is especially common for on-device files and needs a short,
        // unobtrusive blend until proper BPM/phrase analysis is available.
        djBlendDurationMs(current, next)
    } else if (normalized.djSoftMode || djEnabled) {
        val energyDistance = abs(current.energy - next.energy)
        val vocalOverlap = (current.vocal + next.vocal) / 2
        when {
            vocalOverlap >= 70 -> base - 1_500L
            energyDistance <= 15 && (current.energy > 0 || next.energy > 0) -> base + 1_500L
            energyDistance >= 45 -> base - 1_000L
            else -> base
        }
    } else base
    val transition = adaptive
        .coerceIn(MIN_TRANSITION_MS, if (automaticDjMix) MAX_DJ_TRANSITION_MS else MAX_TRANSITION_MS)
        .coerceAtMost(current.durationMs / 4L)
        .let { if (next.durationMs > 0L) it.coerceAtMost(next.durationMs / 2L) else it }
    if (transition < MIN_TRANSITION_MS) return null
    return AutoMixPlan(
        transitionMs = transition,
        preloadLeadMs = (transition + if (automaticDjMix) DJ_PRELOAD_MARGIN_MS else PRELOAD_MARGIN_MS)
            .coerceAtMost(if (automaticDjMix) MAX_DJ_PRELOAD_LEAD_MS else MAX_PRELOAD_LEAD_MS)
    )
}

/**
 * Conservative duration selection until we have a measured beat grid and vocal activity.
 * Do not interpret Track.energy as BPM or assume absent tags mean an instrumental.
 */
internal fun djBlendDurationMs(current: Track, next: Track): Long {
    val missingAnalysis = (current.energy <= 0 && current.vocal <= 0) ||
        (next.energy <= 0 && next.vocal <= 0)
    val energyDistance = abs(current.energy - next.energy)
    val simultaneousVocals = current.vocal >= 70 && next.vocal >= 70
    return when {
        missingAnalysis -> 9_000L
        simultaneousVocals -> 7_000L
        energyDistance >= 45 -> 9_000L
        current.vocal >= 70 || next.vocal >= 70 -> 10_000L
        current.vocal >= 35 && next.vocal >= 35 -> 12_000L
        current.vocal <= 25 && next.vocal <= 25 -> 20_000L
        else -> 14_000L
    }
}

internal fun continuesSameRelease(current: Track, next: Track, shuffleEnabled: Boolean): Boolean {
    val runningOrderKnown = current.trackNumber > 0 && next.trackNumber > 0
    val sameRelease = catalogIdentityMatch(current.albumBrowseId, next.albumBrowseId)
        ?: catalogIdentityMatch(current.upc, next.upc)
        ?: (runningOrderKnown && sameTitledRelease(current, next))
    if (!sameRelease) return false
    return if (runningOrderKnown) followsInRunningOrder(current, next) else !shuffleEnabled
}

private fun catalogIdentityMatch(first: String, second: String): Boolean? {
    val left = first.trim()
    val right = second.trim()
    if (left.isEmpty() || right.isEmpty()) return null
    return left.equals(right, ignoreCase = true)
}

private fun sameTitledRelease(current: Track, next: Track): Boolean =
    catalogIdentityMatch(current.album, next.album) == true &&
        catalogIdentityMatch(current.artist, next.artist) == true

private fun followsInRunningOrder(current: Track, next: Track): Boolean {
    val currentDisc = current.discNumber
    val nextDisc = next.discNumber
    val sameDisc = currentDisc == nextDisc || currentDisc <= 0 || nextDisc <= 0
    if (sameDisc && next.trackNumber == current.trackNumber + 1) return true
    return currentDisc > 0 && nextDisc == currentDisc + 1 && next.trackNumber == 1
}

internal fun equalPowerCrossfade(progress: Float): CrossfadeGains {
    val t = progress.coerceIn(0f, 1f)
    val angle = t * (PI.toFloat() / 2f)
    return CrossfadeGains(outgoing = cos(angle), incoming = sin(angle))
}

internal fun djOverlayCrossfade(progress: Float, overlappingVocals: Boolean = false): CrossfadeGains {
    val t = progress.coerceIn(0f, 1f)
    // An easing envelope without the old 65% hold avoids keeping the second
    // track barely audible for most of the mix, followed by an obvious handover.
    // Delay the incoming vocal slightly when both tracks have known strong vocals.
    val adjusted = if (overlappingVocals) ((t - 0.08f) / 0.92f).coerceIn(0f, 1f) else t
    val incoming = adjusted * adjusted * (3f - 2f * adjusted)
    return CrossfadeGains(outgoing = 1f - incoming, incoming = incoming)
}

internal fun crossfadeHandoffNeedsResync(
    primaryPositionMs: Long,
    secondaryPositionMs: Long,
    toleranceMs: Long = 250L
): Boolean = abs(primaryPositionMs.coerceAtLeast(0L) - secondaryPositionMs.coerceAtLeast(0L)) > toleranceMs.coerceAtLeast(0L)

internal fun crossfadeHandoffSeekPosition(
    secondaryPositionMs: Long,
    durationMs: Long,
    leadMs: Long = 120L
): Long {
    val requested = secondaryPositionMs.coerceAtLeast(0L) + leadMs.coerceAtLeast(0L)
    if (durationMs <= 0L) return requested
    return requested.coerceAtMost((durationMs - 1L).coerceAtLeast(0L))
}

internal fun crossfadeStepWallClockMs(
    mediaStepMs: Long,
    playbackSpeed: Float,
    minWallClockMs: Long = 10L
): Long {
    val speed = if (playbackSpeed.isFinite() && playbackSpeed > 0f) playbackSpeed else 1f
    return ((mediaStepMs.toFloat() / speed).toLong()).coerceAtLeast(minWallClockMs)
}

private const val MIN_TRACK_MS = 30_000L
private const val MIN_TRANSITION_MS = 800L
private const val MAX_TRANSITION_MS = 12_000L
private const val MAX_DJ_TRANSITION_MS = 30_000L
private const val PRELOAD_MARGIN_MS = 8_000L
private const val DJ_PRELOAD_MARGIN_MS = 14_000L
private const val MAX_PRELOAD_LEAD_MS = 20_000L
private const val MAX_DJ_PRELOAD_LEAD_MS = 40_000L
