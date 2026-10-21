package com.luc4n3x.levyra.player

import com.luc4n3x.levyra.domain.LevyraAudioSettings
import com.luc4n3x.levyra.domain.RepeatMode
import com.luc4n3x.levyra.domain.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoMixPlannerTest {
    @Test
    fun crossfadeUsesEqualPowerCurve() {
        val start = equalPowerCrossfade(0f)
        val middle = equalPowerCrossfade(.5f)
        val end = equalPowerCrossfade(1f)

        assertEquals(1f, start.outgoing, .001f)
        assertEquals(0f, start.incoming, .001f)
        assertEquals(0.7071f, middle.outgoing, .001f)
        assertEquals(0.7071f, middle.incoming, .001f)
        assertEquals(0f, end.outgoing, .001f)
        assertEquals(1f, end.incoming, .001f)
    }

    @Test
    fun nativeVideoRepeatOneAndLowRamNeverStartSecondPlayer() {
        val settings = LevyraAudioSettings(crossfadeSeconds = 6)
        assertNull(planAutoMix(track(), track("next"), settings, RepeatMode.Off, videoMode = true, lowRam = false))
        assertNull(planAutoMix(track(), track("next"), settings, RepeatMode.One, videoMode = false, lowRam = false))
        assertNull(planAutoMix(track(), track("next"), settings, RepeatMode.Off, videoMode = false, lowRam = true))
    }

    @Test
    fun tooShortIncomingTracksCannotCreateAnInvalidSecondPlayerTransition() {
        assertNull(planAutoMix(
            track(), track("short").copy(durationMs = 1_200L),
            crossfade, RepeatMode.Off, videoMode = false, lowRam = false
        ))
        assertEquals(800L, planAutoMix(
            track(), track("boundary").copy(durationMs = 1_600L),
            crossfade, RepeatMode.Off, videoMode = false, lowRam = false
        )?.transitionMs)
    }

    @Test
    fun djAutomaticallyMixesWithoutManualCrossfadeSetting() {
        val plan = planAutoMix(
            track("old").copy(energy = 0, vocal = 0),
            track("new").copy(energy = 0, vocal = 0),
            LevyraAudioSettings(crossfadeSeconds = 0),
            RepeatMode.Off,
            videoMode = false,
            lowRam = false,
            djEnabled = true
        )
        assertNotNull(plan)
        assertEquals(9_000L, plan?.transitionMs)
        assertEquals(23_000L, plan?.preloadLeadMs)
        assertNull(
            planAutoMix(track(), track("next"), LevyraAudioSettings(),
                RepeatMode.Off, videoMode = false, lowRam = false, djEnabled = false)
        )
    }

    @Test
    fun djOverlapKeepsOutgoingMusicAudibleWhileIncomingBegins() {
        val start = djOverlayCrossfade(0f)
        val midpoint = djOverlayCrossfade(0.5f)
        val late = djOverlayCrossfade(0.8f)
        val end = djOverlayCrossfade(1f)
        assertEquals(1f, start.outgoing, 0.001f)
        assertEquals(0f, start.incoming, 0.001f)
        assertEquals(0.5f, midpoint.outgoing, 0.001f)
        assertEquals(0.5f, midpoint.incoming, 0.001f)
        assertTrue(late.incoming > midpoint.incoming)
        assertTrue(late.outgoing < midpoint.outgoing)
        assertEquals(0f, end.outgoing, 0.001f)
        assertEquals(1f, end.incoming, 0.001f)
    }

    @Test
    fun djShortensOverlapForClashingSongsOrShortTracks() {
        val energetic = planAutoMix(
            track().copy(energy = 0),
            track("next").copy(energy = 95),
            LevyraAudioSettings(),
            RepeatMode.Off, videoMode = false, lowRam = false, djEnabled = true
        )
        assertEquals(9_000L, energetic?.transitionMs)
        val short = planAutoMix(
            track().copy(durationMs = 64_000L),
            track("next"),
            LevyraAudioSettings(),
            RepeatMode.Off, videoMode = false, lowRam = false, djEnabled = true
        )
        assertEquals(12_000L, short?.transitionMs)
    }

    @Test
    fun localDjSoftModeStartsLongMixWithoutRadioOrManualCrossfade() {
        val plan = planAutoMix(
            track("local-a"), track("local-b"),
            LevyraAudioSettings(crossfadeSeconds = 0, djSoftMode = true),
            RepeatMode.Off, videoMode = false, lowRam = false
        )
        assertEquals(12_000L, plan?.transitionMs)
        assertEquals(26_000L, plan?.preloadLeadMs)
        assertNull(planAutoMix(
            track(), track("next"),
            LevyraAudioSettings(crossfadeSeconds = 0, djSoftMode = true, gaplessEnabled = false),
            RepeatMode.Off, videoMode = false, lowRam = false
        ))
    }

    @Test
    fun overlappingStrongVocalsShortenTheDjAndDelayTheIncomingVoice() {
        val plan = planAutoMix(
            track().copy(vocal = 85), track("next").copy(vocal = 90),
            LevyraAudioSettings(djSoftMode = true),
            RepeatMode.Off, videoMode = false, lowRam = false
        )
        assertEquals(7_000L, plan?.transitionMs)

        val normal = djOverlayCrossfade(0.5f)
        val voices = djOverlayCrossfade(0.5f, overlappingVocals = true)
        assertTrue(voices.incoming < normal.incoming)
        assertTrue(voices.outgoing > normal.outgoing)
    }

    @Test
    fun missingLocalAudioAnalysisIsNotMistakenForInstrumentalMusic() {
        val unknown = track("local").copy(energy = 0, vocal = 0)
        val known = track("stream").copy(energy = 60, vocal = 20)
        assertEquals(9_000L, djBlendDurationMs(unknown, known))
        assertEquals(9_000L, djBlendDurationMs(known, unknown))
    }

    @Test
    fun instrumentalMetadataPermitsLongerMixesButVocalsDoNot() {
        assertEquals(
            20_000L,
            djBlendDurationMs(
                track("instrumental-a").copy(energy = 60, vocal = 10),
                track("instrumental-b").copy(energy = 65, vocal = 15)
            )
        )
        assertEquals(
            12_000L,
            djBlendDurationMs(
                track("voices-a").copy(energy = 60, vocal = 55),
                track("voices-b").copy(energy = 65, vocal = 55)
            )
        )
    }

    @Test
    fun djEnvelopeHasNoLateJumpAndCrossesSmoothlyAtTheMiddle() {
        val middle = djOverlayCrossfade(0.5f)
        val left = djOverlayCrossfade(0.49f)
        val right = djOverlayCrossfade(0.51f)
        assertEquals(0.5f, middle.incoming, 0.001f)
        assertTrue(middle.incoming - left.incoming < 0.025f)
        assertTrue(right.incoming - middle.incoming < 0.025f)
        assertTrue(djOverlayCrossfade(0.25f).incoming > 0.12f)
        assertTrue(djOverlayCrossfade(0.75f).incoming > 0.8f)
    }

    @Test
    fun djOverlayGainsDoNotIncreaseCombinedOutputAboveUnity() {
        for (step in 0..100) {
            for (vocals in listOf(false, true)) {
                val gain = djOverlayCrossfade(step / 100f, overlappingVocals = vocals)
                assertTrue(gain.outgoing in 0f..1f)
                assertTrue(gain.incoming in 0f..1f)
                assertEquals(1f, gain.outgoing + gain.incoming, 0.001f)
            }
        }
    }

    @Test
    fun djDoesNotCreateSecondPlayerDuringNativeVideoOrLowMemory() {
        val settings = LevyraAudioSettings()
        assertNull(planAutoMix(track(), track("next"), settings,
            RepeatMode.Off, videoMode = true, lowRam = false, djEnabled = true))
        assertNull(planAutoMix(track(), track("next"), settings,
            RepeatMode.Off, videoMode = false, lowRam = true, djEnabled = true))
    }

    @Test
    fun autoMixAdaptsDurationWithoutExceedingBounds() {
        val current = track().copy(energy = 70, vocal = 30)
        val next = track("next").copy(energy = 75, vocal = 30)
        val plan = planAutoMix(
            current,
            next,
            LevyraAudioSettings(crossfadeSeconds = 6, djSoftMode = true),
            RepeatMode.Off,
            videoMode = false,
            lowRam = false
        )
        assertEquals(7_500L, plan?.transitionMs)
    }

    @Test
    fun consecutiveTracksOfSameCatalogReleaseSkipCrossfade() {
        val current = albumTrack("a", track = 3)
        val next = albumTrack("b", track = 4)

        assertNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = false))
    }

    @Test
    fun discBoundaryOfSameReleaseStaysContinuous() {
        val current = albumTrack("a", track = 12, disc = 1)
        val next = albumTrack("b", track = 1, disc = 2)

        assertNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = false))
    }

    @Test
    fun sameReleaseOutOfRunningOrderStillCrossfades() {
        val current = albumTrack("a", track = 3)
        val next = albumTrack("b", track = 7)

        assertNotNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = false))
    }

    @Test
    fun leavingAlbumIntoRadioStillCrossfades() {
        val current = albumTrack("a", track = 10)
        val next = albumTrack("b", track = 11).copy(albumBrowseId = "MPREb_other")

        assertNotNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = false))
    }

    @Test
    fun catalogIdentityWithoutTrackNumbersDependsOnShuffle() {
        val current = albumTrack("a", track = 0)
        val next = albumTrack("b", track = 0)

        assertNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = false, shuffleEnabled = false))
        assertNotNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = false, shuffleEnabled = true))
    }

    @Test
    fun upcIdentifiesReleaseWhenBrowseIdIsMissing() {
        val current = albumTrack("a", track = 5).copy(albumBrowseId = "", upc = "0602435000000")
        val next = albumTrack("b", track = 6).copy(albumBrowseId = "", upc = "0602435000000")

        assertNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = false))
    }

    @Test
    fun conflictingUpcOverridesMatchingTitles() {
        val current = track("a").copy(album = "Album", artist = "Artist", upc = "111", trackNumber = 4)
        val next = track("b").copy(album = "Album", artist = "Artist", upc = "222", trackNumber = 5)

        assertNotNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = false))
    }

    @Test
    fun albumTitleAloneIsNotTrustedWithoutRunningOrder() {
        val current = track("a").copy(album = "Greatest Hits", artist = "Band")
        val next = track("b").copy(album = "Greatest Hits", artist = "Band")

        assertNotNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = false))
    }

    @Test
    fun albumTitleArtistAndRunningOrderIdentifyReleaseWithoutCatalogId() {
        val current = track("a").copy(album = "Live at Wembley", artist = "Band", trackNumber = 2)
        val next = track("b").copy(album = " live at wembley ", artist = "BAND", trackNumber = 3)

        assertNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = false))
    }

    @Test
    fun sameTitleByDifferentArtistStillCrossfades() {
        val current = track("a").copy(album = "Greatest Hits", artist = "Band", trackNumber = 2)
        val next = track("b").copy(album = "Greatest Hits", artist = "Other Band", trackNumber = 3)

        assertNotNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = false))
    }

    @Test
    fun existingGuardsStillWinForSameReleasePairs() {
        val current = albumTrack("a", track = 3)
        val next = albumTrack("b", track = 7)

        assertNull(planAutoMix(current, next, crossfade, RepeatMode.One, videoMode = false, lowRam = false))
        assertNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = true, lowRam = false))
        assertNull(planAutoMix(current, next, crossfade, RepeatMode.Off, videoMode = false, lowRam = true))
    }

    @Test
    fun handoffResyncOnlyWhenPlayersDriftPastTolerance() {
        assertFalse(crossfadeHandoffNeedsResync(10_000L, 10_180L, toleranceMs = 250L))
        assertTrue(crossfadeHandoffNeedsResync(10_000L, 10_400L, toleranceMs = 250L))
    }

    @Test
    fun handoffSeekTargetsLiveSecondaryPositionAndClampsToDuration() {
        assertEquals(12_120L, crossfadeHandoffSeekPosition(12_000L, 180_000L, leadMs = 120L))
        assertEquals(19_999L, crossfadeHandoffSeekPosition(19_950L, 20_000L, leadMs = 120L))
    }

    @Test
    fun crossfadeStepScalesInverselyWithPlaybackSpeed() {
        assertEquals(50L, crossfadeStepWallClockMs(50L, 1.0f))
        assertEquals(25L, crossfadeStepWallClockMs(50L, 2.0f))
        assertEquals(100L, crossfadeStepWallClockMs(50L, 0.5f))
        assertEquals(40L, crossfadeStepWallClockMs(60L, 1.5f))
    }

    @Test
    fun crossfadeStepClampsToMinimumAndHandlesInvalidSpeedGracefully() {
        assertEquals(10L, crossfadeStepWallClockMs(5L, 2.0f, minWallClockMs = 10L))
        assertEquals(50L, crossfadeStepWallClockMs(50L, 0f))
        assertEquals(50L, crossfadeStepWallClockMs(50L, -1.5f))
        assertEquals(50L, crossfadeStepWallClockMs(50L, Float.NaN))
        assertEquals(50L, crossfadeStepWallClockMs(50L, Float.POSITIVE_INFINITY))
    }

    private val crossfade = LevyraAudioSettings(crossfadeSeconds = 6)

    private fun albumTrack(id: String, track: Int, disc: Int = 1) = track(id).copy(
        albumBrowseId = "MPREb_release",
        trackNumber = track,
        discNumber = disc
    )

    private fun track(id: String = "current") = Track(
        id = id,
        title = id,
        artist = "Artist",
        album = "Album",
        durationMs = 180_000L,
        streamUrl = "",
        videoUrl = "",
        thumbnailUrl = "",
        largeThumbnailUrl = "",
        source = "test",
        moodTags = emptySet(),
        energy = 50,
        vocal = 50,
        replayScore = 0,
        cacheScore = 0,
        accentStart = 0,
        accentEnd = 0
    )
}
