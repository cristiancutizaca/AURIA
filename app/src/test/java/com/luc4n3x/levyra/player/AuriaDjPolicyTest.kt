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
            local = false,
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
            local = false,
            liveRadio = false
        ))
    }

    @Test
    fun preservesPlaylistLoopAndNonMusicModes() {
        fun decide(loop: Boolean = false, video: Boolean = false, local: Boolean = false, live: Boolean = false) =
            auriaDjForPlayback(
                existing = true,
                manuallyDisabled = false,
                loopingPlaylist = loop,
                videoMode = video,
                local = local,
                liveRadio = live
            )
        assertFalse(decide(loop = true))
        assertFalse(decide(video = true))
        assertFalse(decide(local = true))
        assertFalse(decide(live = true))
        assertTrue(decide())
    }
}
