package com.luc4n3x.levyra.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuriaDjDiscoveryQueriesTest {
    @Test
    fun locallyPlayingArtistIsTheFirstDiscoverySeed() {
        val queries = AuriaDjDiscoveryQueries.plan(
            seed = track("Now Playing", "YOASOBI"),
            localSongs = listOf(track("One", "Ado"), track("Two", "Ado")),
            recent = listOf(track("Three", "Ado")),
            favorites = listOf(track("Four", "Ado")),
            limit = 2
        )

        assertEquals(listOf("YOASOBI music", "Ado music"), queries)
    }

    @Test
    fun offlineLibraryAndFavoritesCanSeedOnlineMusicWithoutMatchingTheCurrentSong() {
        val queries = AuriaDjDiscoveryQueries.plan(
            seed = track("Unknown", "<unknown>"),
            localSongs = listOf(track("Local One", "M83")),
            recent = emptyList(),
            favorites = listOf(track("Loved", "Daft Punk")),
            limit = 2
        )

        assertEquals(listOf("Daft Punk music", "M83 music"), queries)
    }

    @Test
    fun duplicateArtistsDoNotProduceDuplicateNetworkQueries() {
        val queries = AuriaDjDiscoveryQueries.plan(
            seed = track("Playing", "Ado"),
            localSongs = listOf(track("First", "ado"), track("Second", "ADO")),
            recent = listOf(track("Recent", "Ado")),
            favorites = listOf(track("Favorite", "Ado")),
            limit = 5
        )

        assertEquals(1, queries.size)
        assertEquals("Ado music", queries.single())
    }

    @Test
    fun missingArtistFallsBackToSongTitleAndZeroLimitSkipsAllSearches() {
        val seed = track("Midnight City", "Unknown Artist")
        assertEquals(
            listOf("Midnight City music"),
            AuriaDjDiscoveryQueries.plan(seed, emptyList(), emptyList(), emptyList())
        )
        assertTrue(AuriaDjDiscoveryQueries.plan(seed, emptyList(), emptyList(), emptyList(), limit = 0).isEmpty())
    }

    private fun track(title: String, artist: String) = Track(
        id = "${artist}_${title}",
        title = title,
        artist = artist,
        album = "",
        durationMs = 180_000L,
        streamUrl = "",
        videoUrl = "",
        thumbnailUrl = "",
        largeThumbnailUrl = "",
        source = "test",
        moodTags = emptySet(),
        energy = 0,
        vocal = 0,
        replayScore = 0,
        cacheScore = 0,
        accentStart = 0,
        accentEnd = 0
    )
}
