package com.luc4n3x.levyra.desktop.core.dj

import com.luc4n3x.levyra.desktop.core.model.Track

object AuriaDjPlanner {
    private data class Rated(val track: Track, val score: Int)

    fun recommend(
        seed: Track,
        queued: List<Track>,
        related: List<Track>,
        searched: List<Track>,
        local: List<Track>,
        favorites: List<Track>,
        history: List<Track>,
        limit: Int = 8,
        learning: DjLearningData = DjLearningData()
    ): List<Track> {
        if (limit <= 0) return emptyList()
        val knownIds = queued.mapTo(HashSet()) { it.id }
        val knownSongs = queued.mapTo(HashSet(), AuriaDjIdentity::songKey)
        val likedIds = favorites.mapTo(HashSet()) { it.id }
        val recentIds = history.take(25).mapTo(HashSet()) { it.id }
        val seedArtist = AuriaDjIdentity.artistKey(seed.artist)
        val learnedScore = AuriaDjLearningRanker.scores(learning)
        val scored = LinkedHashMap<String, Rated>()

        fun consider(tracks: List<Track>, base: Int) {
            tracks.forEach { track ->
                if (track.id.isBlank() || track.id in knownIds ||
                    (track.videoUrl.isBlank() && !track.isLocalFile)
                ) return@forEach
                val song = AuriaDjIdentity.songKey(track)
                if (song in knownSongs) return@forEach
                val artist = AuriaDjIdentity.artistKey(track.artist)
                val sameArtist = seedArtist.isNotBlank() && artist == seedArtist
                val isFavorite = track.id in likedIds
                val score = base + (if (sameArtist) 32 else 0) +
                    (if (isFavorite) 20 else 0) +
                    (if (track.isLocalFile) 8 else 0) -
                    (if (track.id in recentIds) 35 else 0) +
                    learnedScore(track)
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
                val artist = AuriaDjIdentity.artistKey(candidate.track.artist)
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
            lastArtists += AuriaDjIdentity.artistKey(best.track.artist)
            if (lastArtists.size > 2) lastArtists.removeAt(0)
        }
        return result
    }

}
