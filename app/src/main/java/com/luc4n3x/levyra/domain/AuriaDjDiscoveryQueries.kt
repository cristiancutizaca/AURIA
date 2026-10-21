package com.luc4n3x.levyra.domain

import java.util.Locale

object AuriaDjDiscoveryQueries {
    private const val MAX_LOCAL_SAMPLES = 240

    fun plan(
        seed: Track,
        localSongs: List<Track>,
        recent: List<Track>,
        favorites: List<Track>,
        limit: Int = 2
    ): List<String> {
        if (limit <= 0) return emptyList()
        val names = LinkedHashMap<String, String>()
        val scores = HashMap<String, Int>()

        fun add(artist: String, weight: Int) {
            val name = ListeningSignalEngine.splitArtists(artist).firstOrNull()?.trim().orEmpty()
            val key = name.lowercase(Locale.ROOT)
            if (key.length < 2 || key in UNKNOWN_ARTISTS) return
            names.putIfAbsent(key, name)
            scores[key] = (scores[key] ?: 0) + weight
        }

        add(seed.artist, 80)
        favorites.take(24).forEach { add(it.artist, 12) }
        recent.take(30).forEach { add(it.artist, 8) }

        if (localSongs.isNotEmpty()) {
            val stride = ((localSongs.size.toLong() + MAX_LOCAL_SAMPLES - 1L) / MAX_LOCAL_SAMPLES)
                .coerceAtLeast(1L).toInt()
            localSongs.forEachIndexed { index, track ->
                if (index % stride == 0) add(track.artist, 2)
            }
        }

        val anchor = ListeningSignalEngine.splitArtists(seed.artist).firstOrNull()
            ?.trim()?.lowercase(Locale.ROOT).orEmpty()
        val ordered = scores.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { it.key }
            .let { candidates ->
                if (anchor in names) listOf(anchor) + candidates.filterNot { it == anchor } else candidates
            }
        val queries = ordered.distinct().take(limit).mapNotNull { names[it]?.let { name -> "${name} music" } }
        if (queries.isNotEmpty()) return queries
        return seed.title.trim().takeIf { it.length >= 3 }
            ?.let { listOf("${it} music") }
            .orEmpty()
    }

    private val UNKNOWN_ARTISTS = setOf(
        "unknown", "unknown artist", "artista desconocido", "<unknown>", "various artists"
    )
}
