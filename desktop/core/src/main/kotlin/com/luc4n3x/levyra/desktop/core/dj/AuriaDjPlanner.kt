package com.luc4n3x.levyra.desktop.core.dj

import com.luc4n3x.levyra.desktop.core.model.Track
import java.util.Locale

object AuriaDjPlanner {
    private data class Rated(val track: Track, val score: Int)

    private val titleDecorations = Regex(
        """(?i)\s*[\[(](?:official|music|audio|video|lyrics|live|remaster(?:ed)?|visualizer|hd|hq|4k|mv|feat\.?)[^\])]*[\])]"""
    )
    private val nonLetters = Regex("""[^\p{L}\p{N}]+""")

    fun recommend(
        seed: Track,
        queued: List<Track>,
        related: List<Track>,
        searched: List<Track>,
        local: List<Track>,
        favorites: List<Track>,
        history: List<Track>,
        limit: Int = 8
    ): List<Track> {
        if (limit <= 0) return emptyList()
        val knownIds = queued.mapTo(HashSet()) { it.id }
        val knownSongs = queued.mapTo(HashSet(), ::songIdentity)
        val likedIds = favorites.mapTo(HashSet()) { it.id }
        val recentIds = history.take(25).mapTo(HashSet()) { it.id }
        val seedArtist = artistIdentity(seed.artist)
        val scored = LinkedHashMap<String, Rated>()

        fun consider(tracks: List<Track>, base: Int) {
            tracks.forEach { track ->
                if (track.id.isBlank() || track.id in knownIds ||
                    (track.videoUrl.isBlank() && !track.isLocalFile)
                ) return@forEach
                val song = songIdentity(track)
                if (song in knownSongs) return@forEach
                val artist = artistIdentity(track.artist)
                val sameArtist = seedArtist.isNotBlank() && artist == seedArtist
                val isFavorite = track.id in likedIds
                val score = base + (if (sameArtist) 32 else 0) +
                    (if (isFavorite) 20 else 0) +
                    (if (track.isLocalFile) 8 else 0) -
                    (if (track.id in recentIds) 35 else 0)
                val existing = scored[song]
                if (existing == null || score > existing.score) {
                    scored[song] = Rated(track, score)
                }
            }
        }

        consider(related, 60)
        consider(searched, 30)
        consider(local, 23)
        consider(favorites, 12)
        consider(history, 3)

        val pending = scored.values.toMutableList()
        val result = ArrayList<Track>(minOf(limit, pending.size))
        val lastArtists = ArrayList<String>(2)
        while (pending.isNotEmpty() && result.size < limit) {
            val best = pending.maxByOrNull { candidate ->
                val artist = artistIdentity(candidate.track.artist)
                val diversityPenalty = when {
                    artist.isBlank() -> 0
                    lastArtists.lastOrNull() == artist -> 24
                    artist in lastArtists -> 10
                    else -> 0
                }
                candidate.score - diversityPenalty
            } ?: break
            result += best.track
            pending.remove(best)
            lastArtists += artistIdentity(best.track.artist)
            if (lastArtists.size > 2) lastArtists.removeAt(0)
        }
        return result
    }

    private fun artistIdentity(artist: String): String =
        artist.trim().lowercase(Locale.ROOT)
            .substringBefore(" feat")
            .substringBefore(" & ")
            .trim()

    private fun songIdentity(track: Track): String {
        val artist = artistIdentity(track.artist)
        val title = nonLetters.replace(
            titleDecorations.replace(track.title.lowercase(Locale.ROOT), ""),
            " "
        ).trim()
        return "$artist|$title"
    }
}
