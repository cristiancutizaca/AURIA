package com.luc4n3x.levyra.player

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuriaDjPolicyTest {
    @Test
    fun continuousDjIsEnabledForOrdinaryPlayback() {
        assertTrue(auriaDjForPlayback(
            manuallyDisabled = false,
            loopingPlaylist = false,
            videoMode = false,
            liveRadio = false
        ))
    }

    @Test
    fun explicitDisableAlwaysWins() {
        assertFalse(auriaDjForPlayback(
            manuallyDisabled = true,
            loopingPlaylist = false,
            videoMode = false,
            liveRadio = false
        ))
    }

    @Test
    fun albumLoopVideoAndLiveRadioDoNotTriggerSongDj() {
        fun decide(loop: Boolean = false, video: Boolean = false, live: Boolean = false) =
            auriaDjForPlayback(
                manuallyDisabled = false,
                loopingPlaylist = loop,
                videoMode = video,
                liveRadio = live
            )
        assertFalse(decide(loop = true))
        assertFalse(decide(video = true))
        assertFalse(decide(live = true))
        assertTrue(decide())
    }
}
