package com.luc4n3x.levyra.desktop.player

import com.luc4n3x.levyra.desktop.core.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CrossfadePlannerTest {

    private fun track(album: String = "", durationMs: Long = 240_000L, artist: String = "Artist") = Track(
        id = album + durationMs,
        title = "Title",
        artist = artist,
        album = album,
        videoUrl = "https://example.org",
        durationMs = durationMs
    )

    @Test
    fun requestedDurationIsClampedToHalfOfTheShorterTrack() {
        assertEquals(6_000L, CrossfadePlanner.effectiveDurationMs(6_000, 240_000L, 200_000L))
        assertEquals(9_000L, CrossfadePlanner.effectiveDurationMs(12_000, 18_000L, 200_000L))
        assertEquals(5_000L, CrossfadePlanner.effectiveDurationMs(12_000, 200_000L, 10_000L))
    }

    @Test
    fun tooShortOrDisabledTransitionsResolveToZero() {
        assertEquals(0L, CrossfadePlanner.effectiveDurationMs(0, 240_000L, 240_000L))
        assertEquals(0L, CrossfadePlanner.effectiveDurationMs(6_000, 0L, 240_000L))
        assertEquals(0L, CrossfadePlanner.effectiveDurationMs(6_000, 800L, 240_000L))
    }

    @Test
    fun djAutoMixUsesFourSecondsWhenTheDjIsActive() {
        assertEquals(4_000, CrossfadePlanner.requestedDjCrossfadeMs(0, 4_000, true))
        assertEquals(0, CrossfadePlanner.requestedDjCrossfadeMs(0, 4_000, false))
        assertEquals(0, CrossfadePlanner.requestedDjCrossfadeMs(0, 0, true))
        assertEquals(8_000, CrossfadePlanner.requestedDjCrossfadeMs(8_000, 4_000, true))
        assertEquals(
            0L,
            CrossfadePlanner.transitionDurationMs(
                requestedMs = 4_000,
                smartCrossfade = true,
                current = track(album = "Collection"),
                next = track(album = "Collection"),
                currentDurationMs = 240_000L
            )
        )
    }

    @Test
    fun smartCrossfadeSkipsConsecutiveTracksOfTheSameAlbum() {
        val current = track(album = "Blurred")
        val next = track(album = "blurred")

        assertTrue(CrossfadePlanner.sharesAlbum(current, next))
        assertEquals(
            0L,
            CrossfadePlanner.transitionDurationMs(6_000, true, current, next, 240_000L)
        )
        assertEquals(
            6_000L,
            CrossfadePlanner.transitionDurationMs(6_000, false, current, next, 240_000L)
        )
    }

    @Test
    fun identicalAlbumTitlesFromDifferentArtistsMustStillMix() {
        val previous = track(album = "Greatest Hits", artist = "First Artist")
        val next = track(album = "greatest hits", artist = "Second Artist")

        assertFalse(CrossfadePlanner.sharesAlbum(previous, next))
        assertEquals(27_000L, CrossfadePlanner.transitionDurationMs(27_000, true, previous, next, 240_000L))
    }

    @Test
    fun albumWithoutBothArtistNamesCannotSafelyDisableAutomix() {
        val unknown = track(album = "Greatest Hits", artist = "")
        val known = track(album = "Greatest Hits", artist = "Known Artist")
        assertFalse(CrossfadePlanner.sharesAlbum(unknown, known))
        assertFalse(CrossfadePlanner.sharesAlbum(known, unknown))
    }

    @Test
    fun sameArtistAndAlbumIgnoreWhitespaceAndCase() {
        val previous = track(album = "  Album Name ", artist = " ARTIST ")
        val next = track(album = "album name", artist = "artist")
        assertTrue(CrossfadePlanner.sharesAlbum(previous, next))
        assertEquals(0L, CrossfadePlanner.transitionDurationMs(27_000, true, previous, next, 240_000L))
    }

    @Test
    fun unknownAlbumsNeverCountAsTheSameAlbum() {
        assertFalse(CrossfadePlanner.sharesAlbum(track(album = ""), track(album = "")))
        assertEquals(
            6_000L,
            CrossfadePlanner.transitionDurationMs(6_000, true, track(), track(), 240_000L)
        )
    }

    @Test
    fun equalPowerGainsPreserveConstantEnergy() {
        listOf(0f, 0.25f, 0.5f, 0.75f, 1f).forEach { fraction ->
            val outgoing = CrossfadePlanner.outgoingGain(fraction)
            val incoming = CrossfadePlanner.incomingGain(fraction)
            assertEquals(1.0, (outgoing * outgoing + incoming * incoming).toDouble(), 0.0001)
        }
        assertEquals(1f, CrossfadePlanner.outgoingGain(0f), 0.0001f)
        assertEquals(0f, CrossfadePlanner.outgoingGain(1f), 0.0001f)
        assertEquals(0f, CrossfadePlanner.incomingGain(0f), 0.0001f)
        assertEquals(1f, CrossfadePlanner.incomingGain(1f), 0.0001f)
    }

    @Test
    fun djOverlapAllowsOutgoingVoiceToContinueUnderIncomingMusic() {
        assertEquals(1f, CrossfadePlanner.djOutgoingGain(0f), 0.001f)
        assertEquals(0f, CrossfadePlanner.djIncomingGain(0f), 0.001f)
        assertTrue(CrossfadePlanner.djOutgoingGain(0.5f) > 0.75f)
        assertTrue(CrossfadePlanner.djIncomingGain(0.5f) > 0f)
        assertTrue(CrossfadePlanner.djIncomingGain(0.8f) > CrossfadePlanner.djIncomingGain(0.5f))
        assertEquals(0f, CrossfadePlanner.djOutgoingGain(1f), 0.001f)
        assertEquals(1f, CrossfadePlanner.djIncomingGain(1f), 0.001f)
    }

    @Test
    fun longDjOverlapClampsToShorterTrack() {
        assertEquals(27_000L, CrossfadePlanner.effectiveDurationMs(27_000, 207_000L, 161_000L))
        assertEquals(12_000L, CrossfadePlanner.effectiveDurationMs(27_000, 24_000L, 161_000L))
    }

    @Test
    fun djOverlapAvoidsAdditiveGainClipping() {
        for (i in 0..100) {
            val t = i / 100f
            assertEquals(
                1f, CrossfadePlanner.djOutgoingGain(t) + CrossfadePlanner.djIncomingGain(t), 0.001f
            )
        }
    }

    @Test
    fun djOverlapUsesMediaTimeWhenPlaybackSpeedChanges() {
        assertEquals(27_000L, CrossfadePlanner.mixWallClockDurationMs(27_000L, 1f))
        assertEquals(13_500L, CrossfadePlanner.mixWallClockDurationMs(27_000L, 2f))
        assertEquals(54_000L, CrossfadePlanner.mixWallClockDurationMs(27_000L, 0.5f))
        assertEquals(27_000L, CrossfadePlanner.mixWallClockDurationMs(27_000L, Float.NaN))
    }

    @Test
    fun prepareThresholdLeavesRoomForTheTransition() {
        assertEquals(
            240_000L - 6_000L - CrossfadePlanner.PREPARE_LEAD_MS,
            CrossfadePlanner.prepareThresholdMs(240_000L, 6_000L)
        )
        assertEquals(0L, CrossfadePlanner.prepareThresholdMs(1_000L, 6_000L))
        assertEquals(Long.MAX_VALUE, CrossfadePlanner.prepareThresholdMs(0L, 6_000L))
    }
}
