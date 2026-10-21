package com.luc4n3x.levyra.feature.sharedmedia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuriaPlaylistLinksTest {
    @Test
    fun generatedPlaylistsUseTheNewAppSchemeAndStillAcceptLegacyText() {
        val track = LevyraSharedTrack("track_0001", "Local music", "Artist")
        val link = requireNotNull(LevyraPlaylistShareCodec.encodeLink("My playlist", listOf(track)))
        assertTrue(link.startsWith("auria://playlist?"))
        val payload = requireNotNull(LevyraPlaylistShareCodec.extractPayload(link))
        assertEquals(payload, LevyraPlaylistShareCodec.extractPayload(link.replace("auria://", "levyra://")))
        val decoded = LevyraPlaylistShareCodec.decode(payload)
        assertTrue(decoded is LevyraPlaylistDecodeResult.Success)
        assertEquals(track, (decoded as LevyraPlaylistDecodeResult.Success).playlist.tracks.first())
    }
}
