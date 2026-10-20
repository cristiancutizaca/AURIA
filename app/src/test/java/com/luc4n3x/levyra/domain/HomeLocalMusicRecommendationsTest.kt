package com.luc4n3x.levyra.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeLocalMusicRecommendationsTest {
    private fun song(id: String, artist: String, title: String = "Song $id") = Track(
        id = id,
        title = title,
        artist = artist,
        album = "Album",
        durationMs = 180_000,
        streamUrl = "",
        videoUrl = "https://www.youtube.com/watch?v=$id",
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

    @Test
    fun emptyLibraryFallsBackToRegularHome() {
        assertTrue(HomeLocalMusicRecommendations.rank(
            localSongs = emptyList(),
            recent = emptyList(),
            favorites = emptyList(),
            online = listOf(song("web", "Unrelated"))
        ).isEmpty())
    }

    @Test
    fun deviceArtistsDriveRecommendationsNotGenericChartsOrOnboardingGenres() {
        val local = listOf(song("local1", "DragonForce"), song("local2", "DragonForce"),
            song("local3", "Sisqo"))
        val similar = song("recommended", "DragonForce")
        val unrelated = song("random", "Taylor Swift")
        val ranked = HomeLocalMusicRecommendations.rank(
            localSongs = local,
            recent = emptyList(),
            favorites = emptyList(),
            online = listOf(unrelated, similar)
        )
        assertTrue(ranked.any { it.id == "recommended" })
        assertFalse(ranked.any { it.id == "random" })
        assertTrue(ranked.take(3).all { it in local })
    }

    @Test
    fun favoritesAndListeningHistoryImproveArtistWeighting() {
        val local = listOf(song("a1", "Band A"), song("b1", "Band B"))
        val searches = HomeLocalMusicRecommendations.searchQueries(
            localSongs = local,
            recent = listOf(song("recent", "Band B")),
            favorites = listOf(song("fav", "Band B"))
        )
        assertEquals("band b music", searches.first())
        assertTrue(searches.contains("band a music"))
    }

    @Test
    fun unknownArtistAndDuplicatesAreNotUsedToInferTaste() {
        val ranked = HomeLocalMusicRecommendations.rank(
            localSongs = listOf(song("u", "Unknown Artist"),
                song("a", "Artist A"), song("a2", "Artist A", "Song a")),
            recent = emptyList(),
            favorites = emptyList(),
            online = listOf(song("b", "Unknown Artist"))
        )
        assertTrue(ranked.none { it.artist == "Unknown Artist" })
        assertEquals(ranked.map(LevyraPersonalOrbit::identityKey).toSet().size, ranked.size)
    }

    @Test
    fun samplesAllPartsOfLargeAlphabeticallySortedLibraries() {
        val local = (1..950).map { index ->
            song("id$index", if (index > 900) "ZZZ Artist" else "AAA Artist", "Song $index")
        }
        val queries = HomeLocalMusicRecommendations.searchQueries(
            localSongs = local,
            recent = emptyList(),
            favorites = emptyList(),
            limit = 7
        )
        assertTrue("Artists near the end of a large library must be seen",
            queries.any { it.contains("zzz artist") })
    }

    @Test
    fun neverExceedsDisplayLimit() {
        val songs = (1..40).map { song("id$it", "Band ${it % 5}", "Track $it") }
        val ranked = HomeLocalMusicRecommendations.rank(songs, emptyList(), emptyList(), emptyList(), limit = 7)
        assertEquals(7, ranked.size)
    }
}
