package com.luc4n3x.levyra.desktop.core.dj

import com.luc4n3x.levyra.desktop.core.model.Track
import com.luc4n3x.levyra.desktop.core.storage.AppPaths
import com.luc4n3x.levyra.desktop.core.storage.JsonFileStore
import kotlinx.serialization.Serializable

@Serializable
data class DjSongFeedback(
    val songKey: String,
    val artistKey: String,
    val completions: Int = 0,
    val earlySkips: Int = 0,
    val updatedAt: Long = 0L
)

@Serializable
data class DjLearningData(val songs: List<DjSongFeedback> = emptyList())

class AuriaDjLearningStore(
    private val store: JsonFileStore<DjLearningData>,
    private val nowMillis: () -> Long = System::currentTimeMillis
) {
    @Volatile private var snapshot: DjLearningData = store.read()

    val current: DjLearningData get() = snapshot

    fun recordCompletion(track: Track) = record(track, skipped = false)
    fun recordSkip(track: Track) = record(track, skipped = true)

    @Synchronized
    private fun record(track: Track, skipped: Boolean) {
        if (track.title.isBlank()) return
        val key = AuriaDjIdentity.songKey(track)
        val artist = AuriaDjIdentity.artistKey(track.artist)
        val existing = snapshot.songs.firstOrNull { it.songKey == key }
            ?: DjSongFeedback(songKey = key, artistKey = artist)
        val updated = existing.copy(
            completions = (existing.completions + if (skipped) 0 else 1).coerceAtMost(MAX_COUNT),
            earlySkips = (existing.earlySkips + if (skipped) 1 else 0).coerceAtMost(MAX_COUNT),
            updatedAt = nowMillis()
        )
        val next = DjLearningData(
            songs = (listOf(updated) + snapshot.songs.filterNot { it.songKey == key })
                .take(MAX_SONGS)
        )
        store.write(next)
        snapshot = next
    }

    companion object {
        const val MAX_SONGS = 750
        private const val MAX_COUNT = 100

        fun create(paths: AppPaths) = AuriaDjLearningStore(
            JsonFileStore(
                file = paths.djLearningFile,
                serializer = DjLearningData.serializer(),
                defaultValue = { DjLearningData() }
            )
        )
    }
}

object AuriaDjLearningRanker {
    fun scores(feedback: DjLearningData): (Track) -> Int {
        val bySong = feedback.songs.associateBy(DjSongFeedback::songKey)
        val artistScores = feedback.songs
            .filter { it.artistKey.isNotBlank() }
            .groupBy(DjSongFeedback::artistKey)
            .mapValues { (_, songs) ->
                songs.sumOf { it.completions * 4 - it.earlySkips * 7 }.coerceIn(-40, 40)
            }
        return { track ->
            val songScore = bySong[AuriaDjIdentity.songKey(track)]?.let {
                (it.completions * 8 - it.earlySkips * 20).coerceIn(-80, 40)
            } ?: 0
            songScore + (artistScores[AuriaDjIdentity.artistKey(track.artist)] ?: 0)
        }
    }
}
