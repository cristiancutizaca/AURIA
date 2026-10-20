package com.luc4n3x.levyra.player

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuriaDjPolicyTest {
    @Test
    fun startsRadioForAnOrdinaryOnlineSong() {
        assertTrue(auriaDjForPlayback(
            existing = false,
            manuallyDisabled = false,
            loopingPlaylist = false,
            videoMode = false,
            liveRadio = false
        ))
    }

    @Test
    fun startsContinuousDjFromALocalTrackWhenEnabled() {
        assertTrue(auriaDjForPlayback(
            existing = false,
            manuallyDisabled = false,
            loopingPlaylist = false,
            videoMode = false,
            liveRadio = false
        ))
    }

    @Test
    fun respectsExplicitDisable() {
        assertFalse(auriaDjForPlayback(
            existing = false,
            manuallyDisabled = true,
            loopingPlaylist = false,
            videoMode = false,
            liveRadio = false
        ))
    }

    @Test
    fun preservesPlaylistLoopAndNonMusicModes() {
        fun decide(loop: Boolean = false, video: Boolean = false, live: Boolean = false) =
            auriaDjForPlayback(
                existing = true,
                manuallyDisabled = false,
                loopingPlaylist = loop,
                videoMode = video,
                liveRadio = live
            )
        assertFalse(decide(loop = true))
        assertFalse(decide(video = true))
        assertTrue(decide())
        assertFalse(decide(live = true))
        assertTrue(decide())
    }
}
