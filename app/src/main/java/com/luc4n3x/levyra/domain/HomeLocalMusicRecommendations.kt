package com.luc4n3x.levyra.domain

import java.util.Locale

/**
 * User-library-first home ranking. No onboarding taste choices, remote account,
 * network call or permission is needed; an empty/unavailable library falls back
 * to the normal home discovery feed.
 */
object HomeLocalMusicRecommendations {
    private const val UNKNOWN_ARTIST = "unknown artist"
    private const val UNKNOWN_ARTIST_ES = "artista desconocido"
    private const val MAX_SOURCE = 400

    private fun artistKey(value: String): String = primaryArtistSegment(value)
        .trim().lowercase(Locale.ROOT)
        .takeUnless { it == UNKNOWN_ARTIST || it == UNKNOWN_ARTIST_ES || it == "<unknown>" }
        .orEmpty()

    private fun valid(track: Track): Boolean =
        track.title.isNotBlank() && artistKey(track.artist).length > 1 &&
            LevyraPersonalOrbit.isReliableMusicCandidate(track)

    private fun identity(track: Track): String = LevyraPersonalOrbit.identityKey(track)

    fun searchQueries(
        localSongs: List<Track>,
        recent: List<Track>,
        favorites: List<Track>,
        limit: Int = 7
    ): List<String> {
        if (limit <= 0) return emptyList()
        val artistScore = artistScores(localSongs, recent, favorites)
        return artistScore.entries.sortedWith(
            compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key }
        ).take(limit).map { it.key + " music" }
    }

    fun rank(
        localSongs: List<Track>,
        recent: List<Track>,
        favorites: List<Track>,
        online: List<Track>,
        limit: Int = 20
    ): List<Track> {
        if (limit <= 0) return emptyList()
        val local = localSongs.asSequence().filter(::valid)
            .distinctBy(::identity).take(MAX_SOURCE).toList()
        if (local.isEmpty()) return emptyList()
        val weights = artistScores(local, recent, favorites)
        val favoriteKeys = favorites.mapTo(HashSet(), ::identity)
        val recentlyPlayed = recent.mapTo(HashSet(), ::identity)
        val artistVariety = HashMap<String, Int>()
        val selected = ArrayList<Track>(limit)
        val used = HashSet<String>(limit * 2)
        fun takeCandidate(track: Track): Boolean {
            val key = identity(track)
            if (!used.add(key)) return false
            selected += track
            artistVariety.merge(artistKey(track.artist), 1, Int::plus)
            return true
        }
        val localCandidates = local.sortedWith(
            compareByDescending<Track> { track ->
                val key = identity(track)
                (if (key in recentlyPlayed) 100 else 0) +
                    (if (key in favoriteKeys) 80 else 0) +
                    (weights[artistKey(track.artist)] ?: 0)
            }.thenBy { it.title.lowercase(Locale.ROOT) }
        )
        val localPreviewCount = minOf(limit, 8)
        localCandidates.forEach { track ->
            if (selected.size >= localPreviewCount) return@forEach
            if ((artistVariety[artistKey(track.artist)] ?: 0) >= 3) return@forEach
            takeCandidate(track)
        }
        localCandidates.forEach { if (selected.size < localPreviewCount) takeCandidate(it) }

        // Only recommend online tracks from actual library/listening artists.
        // Generic trending tracks must not displace the user's music.
        online.asSequence()
            .filter(::valid)
            .filter { artistKey(it.artist) in weights }
            .distinctBy(::identity)
            .take(MAX_SOURCE)
            .sortedWith(
                compareByDescending<Track> { track ->
                    (weights[artistKey(track.artist)] ?: 0) * 5 +
                        (if (identity(track) in favoriteKeys) 50 else 0) +
                        (if (identity(track) in recentlyPlayed) 35 else 0)
                }.thenBy { it.title.lowercase(Locale.ROOT) }
            )
            .forEach { if (selected.size < limit) takeCandidate(it) }

        // Offline remains useful even without a connection or a web match.
        localCandidates.forEach { if (selected.size < limit) takeCandidate(it) }
        return selected.take(limit)
    }

    private fun artistScores(
        localSongs: List<Track>, recent: List<Track>, favorites: List<Track>
    ): Map<String, Int> {
        val scores = HashMap<String, Int>()
        val localArtists = localSongs.asSequence().filter(::valid).take(MAX_SOURCE)
            .groupingBy { artistKey(it.artist) }.eachCount()
        for ((artist, count) in localArtists) {
            scores[artist] = minOf(6, count) * 4
        }
        // Recent listening and likes override raw album/file counts.
        for (song in recent.take(36)) {
            val key = artistKey(song.artist)
            if (valid(song) && key.isNotEmpty()) scores.merge(key, 12, Int::plus)
        }
        for (song in favorites.take(36)) {
            val key = artistKey(song.artist)
            if (valid(song) && key.isNotEmpty()) scores.merge(key, 9, Int::plus)
        }
        return scores
    }
}
