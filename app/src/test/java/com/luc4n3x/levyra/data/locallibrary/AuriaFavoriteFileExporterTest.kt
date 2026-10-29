package com.luc4n3x.levyra.data.locallibrary

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuriaFavoriteFileExporterTest {
    @Test
    fun localMediaStoreAudioIsEligible() {
        assertTrue(isAuriaLocalFavoriteSource("Offline", "content://media/external/audio/media/123"))
    }

    @Test
    fun onlineTracksAndOtherUrisAreNeverCopiedAsLocalFiles() {
        assertFalse(isAuriaLocalFavoriteSource("YouTube", "https://example.invalid/song"))
        assertFalse(isAuriaLocalFavoriteSource("Offline", "https://example.invalid/stream"))
        assertFalse(isAuriaLocalFavoriteSource("Offline", "file:///storage/emulated/0/song.mp3"))
    }
}
