package com.luc4n3x.levyra.desktop.core.dj

import com.luc4n3x.levyra.desktop.core.model.Track
import com.luc4n3x.levyra.desktop.core.storage.JsonFileStore
import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuriaDjLearningTest {
    private fun track(id: String, title: String, artist: String) = Track(
        id = id,
        title = title,
        artist = artist,
        videoUrl = "https://www.youtube.com/watch?v=$id"
    )

    @Test
    fun learningPersistsAndMatchesTheSameSongAcrossVideoIds() {
        val folder = Files.createTempDirectory("auria-dj-learning")
        try {
            val file = folder.resolve("dj-learning.json")
            fun open(): AuriaDjLearningStore = AuriaDjLearningStore(
                store = JsonFileStore(file, DjLearningData.serializer(), { DjLearningData() }),
                nowMillis = { 42L }
            )
            val store = open()
            store.recordCompletion(track("id1", "Starlight", "Singer"))
            store.recordSkip(track("id2", "Starlight (Official Audio)", "Singer"))
            val reloaded = open()
            val signal = reloaded.current.songs.single()
            assertEquals(1, signal.completions)
            assertEquals(1, signal.earlySkips)
            assertEquals(42L, signal.updatedAt)
        } finally {
            folder.toFile().deleteRecursively()
        }
    }

    @Test
    fun skipsLowerScoresAndCompletionsRaiseThem() {
        val disliked = track("skip", "Bad", "A")
        val liked = track("like", "Good", "B")
        val data = DjLearningData(
            listOf(
                DjSongFeedback(AuriaDjIdentity.songKey(disliked), "a", earlySkips = 3),
                DjSongFeedback(AuriaDjIdentity.songKey(liked), "b", completions = 4)
            )
        )
        val score = AuriaDjLearningRanker.scores(data)
        assertTrue(score(liked) > score(disliked))
        val result = AuriaDjPlanner.recommend(
            seed = track("seed", "Starting", "Other"),
            queued = listOf(track("seed", "Starting", "Other")),
            related = listOf(disliked, liked),
            searched = emptyList(),
            local = emptyList(),
            favorites = emptyList(),
            history = emptyList(),
            learning = data
        )
        assertEquals("like", result.first().id)
    }

    @Test
    fun historyIsBounded() {
        val folder = Files.createTempDirectory("auria-dj-bounds")
        try {
            val store = AuriaDjLearningStore(
                JsonFileStore(folder.resolve("learn.json"), DjLearningData.serializer(), { DjLearningData() })
            )
            repeat(AuriaDjLearningStore.MAX_SONGS + 3) { i ->
                store.recordCompletion(track("id$i", "Song $i", "Artist"))
            }
            assertEquals(AuriaDjLearningStore.MAX_SONGS, store.current.songs.size)
            assertEquals("Song ${AuriaDjLearningStore.MAX_SONGS + 2}".lowercase(),
                store.current.songs.first().songKey.substringAfter("|"))
        } finally {
            folder.toFile().deleteRecursively()
        }
    }
}
