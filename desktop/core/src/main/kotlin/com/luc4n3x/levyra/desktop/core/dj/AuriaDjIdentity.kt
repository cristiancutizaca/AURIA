package com.luc4n3x.levyra.desktop.core.dj

import com.luc4n3x.levyra.desktop.core.model.Track
import java.util.Locale

object AuriaDjIdentity {
    private val titleDecorations = Regex(
        """(?i)\s*[\[(](?:official|music|audio|video|lyrics|live|remaster(?:ed)?|visualizer|hd|hq|4k|mv|feat\.?)[^\])]*[\])]"""
    )
    private val nonLetters = Regex("""[^\p{L}\p{N}]+""")

    fun artistKey(artist: String): String =
        artist.trim().lowercase(Locale.ROOT)
            .substringBefore(" feat")
            .substringBefore(" & ")
            .trim()

    fun songKey(track: Track): String {
        val artist = artistKey(track.artist)
        val title = nonLetters.replace(
            titleDecorations.replace(track.title.lowercase(Locale.ROOT), ""),
            " "
        ).trim()
        return "$artist|$title"
    }
}
