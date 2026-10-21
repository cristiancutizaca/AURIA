package com.luc4n3x.levyra.desktop.player

import com.luc4n3x.levyra.desktop.core.model.Track
import kotlin.math.cos
import kotlin.math.sin

object CrossfadePlanner {

    const val PREPARE_LEAD_MS = 2_500L
    const val STEP_MS = 60L
    private const val MIN_TRANSITION_MS = 500L
    private const val HALF_PI = (Math.PI / 2.0).toFloat()

    fun effectiveDurationMs(requestedMs: Int, currentDurationMs: Long, nextDurationMs: Long): Long {
        if (requestedMs <= 0 || currentDurationMs <= 0L) return 0L
        var duration = requestedMs.toLong()
        duration = minOf(duration, currentDurationMs / 2L)
        if (nextDurationMs > 0L) {
            duration = minOf(duration, nextDurationMs / 2L)
        }
        return if (duration < MIN_TRANSITION_MS) 0L else duration
    }

    fun sharesAlbum(current: Track, next: Track): Boolean {
        val currentAlbum = current.album.trim()
        val nextAlbum = next.album.trim()
        if (currentAlbum.isEmpty() || nextAlbum.isEmpty()) return false
        return currentAlbum.equals(nextAlbum, ignoreCase = true)
    }

    fun requestedDjCrossfadeMs(
        userCrossfadeMs: Int,
        djMixMs: Int,
        djEnabled: Boolean
    ): Int = when {
        userCrossfadeMs > 0 -> userCrossfadeMs
        djEnabled -> djMixMs.coerceAtLeast(0)
        else -> 0
    }

    fun transitionDurationMs(
        requestedMs: Int,
        smartCrossfade: Boolean,
        current: Track,
        next: Track,
        currentDurationMs: Long
    ): Long {
        if (smartCrossfade && sharesAlbum(current, next)) return 0L
        return effectiveDurationMs(requestedMs, currentDurationMs, next.durationMs)
    }

    fun prepareThresholdMs(durationMs: Long, transitionMs: Long): Long {
        if (durationMs <= 0L) return Long.MAX_VALUE
        val lead = transitionMs + PREPARE_LEAD_MS
        return (durationMs - lead).coerceAtLeast(0L)
    }

    fun mixWallClockDurationMs(mediaDurationMs: Long, playbackSpeed: Float): Long {
        val speed = playbackSpeed.takeIf { it.isFinite() && it > 0f } ?: 1f
        return (mediaDurationMs.toDouble() / speed).toLong().coerceAtLeast(STEP_MS)
    }

    fun outgoingGain(fraction: Float): Float = cos(fraction.coerceIn(0f, 1f) * HALF_PI)

    fun incomingGain(fraction: Float): Float = sin(fraction.coerceIn(0f, 1f) * HALF_PI)

    fun djOutgoingGain(fraction: Float): Float {
        val t = fraction.coerceIn(0f, 1f)
        val phase = if (t <= 0.65f) t / 0.65f else (t - 0.65f) / 0.35f
        val eased = phase * phase * (3f - 2f * phase)
        return if (t <= 0.65f) 1f - 0.25f * eased else 0.75f * (1f - eased)
    }

    fun djIncomingGain(fraction: Float): Float {
        val t = fraction.coerceIn(0f, 1f)
        val phase = if (t <= 0.65f) t / 0.65f else (t - 0.65f) / 0.35f
        val eased = phase * phase * (3f - 2f * phase)
        return if (t <= 0.65f) 0.25f * eased else 0.25f + 0.75f * eased
    }

    fun volumeFor(baseVolume: Int, gain: Float): Int =
        (baseVolume * gain).toInt().coerceIn(0, 100)
}
