package com.luc4n3x.levyra.desktop.core.dj

import com.luc4n3x.levyra.desktop.core.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuriaDjPlannerTest {
    private fun online(id: String, title: String, artist: String) = Track(
        id = id,
        title = title,
        artist = artist,
        videoUrl = "https://www.youtube.com/watch?v=$id"
    )

    @Test
    fun avoidsQueueDuplicatesAndTheSameSongFromOtherSources() {
        val seed = online("one", "Morning", "Miku")
        val clone = online("another", "Morning (Official Audio)", "Miku")
        val next = online("two", "Evening", "Miku")
        val result = AuriaDjPlanner.recommend(
            seed = seed,
            queued = listOf(seed),
            related = listOf(seed, clone, next),
            searched = listOf(next),
            local = emptyList(),
            favorites = emptyList(),
            history = emptyList()
        )
        assertEquals(listOf("two"), result.map { it.id })
    }

    @Test
    fun mixesLocalAndOnlineCandidatesAndPrioritizesSimilarArtists() {
        val seed = online("one", "First", "Artist A")
        val unrelated = online("two", "Random", "Artist B")
        val local = Track(
            id = "local:one",
            title = "Another",
            artist = "Artist A",
            videoUrl = "",
            offlinePath = "C:/Music/another.flac"
        )
        val result = AuriaDjPlanner.recommend(
            seed = seed,
            queued = listOf(seed),
            related = emptyList(),
            searched = listOf(unrelated),
            local = listOf(local),
            favorites = listOf(local),
            history = emptyList()
        )
        assertEquals(local.id, result.first().id)
        assertTrue(result.any { it.id == unrelated.id })
    }

    @Test
    fun deprioritizesRecentlyPlayedWhenAlternativesExist() {
        val seed = online("one", "First", "Artist")
        val repeated = online("old", "Old", "Artist")
        val fresh = online("fresh", "Fresh", "Artist")
        val result = AuriaDjPlanner.recommend(
            seed = seed,
            queued = listOf(seed),
            related = listOf(repeated, fresh),
            searched = emptyList(),
            local = emptyList(),
            favorites = emptyList(),
            history = listOf(repeated)
        )
        assertEquals("fresh", result.first().id)
    }

    @Test
    fun ignoresCandidatesWithoutAPlayableSourceAndRespectsLimits() {
        val seed = online("one", "First", "Artist")
        val broken = seed.copy(id = "broken", title = "Broken", videoUrl = "")
        val result = AuriaDjPlanner.recommend(
            seed = seed,
            queued = listOf(seed),
            related = listOf(broken, online("two", "Second", "Artist"), online("three", "Third", "Artist")),
            searched = emptyList(),
            local = emptyList(),
            favorites = emptyList(),
            history = emptyList(),
            limit = 1
        )
        assertFalse(result.any { it.id == "broken" })
        assertEquals(1, result.size)
    }
}
