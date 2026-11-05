package com.luc4n3x.levyra.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.luc4n3x.levyra.feature.recognition.RecognitionHistoryEntry

// This is a virtual library folder of recognized song metadata, not downloaded MP3 files.
// The persisted recognition history is the source of truth.
internal fun recognizedLibraryTitle(languageCode: String): String =
    if (languageCode.substringBefore('-').equals("es", ignoreCase = true)) "Identificadas" else "Recognized"

private fun recognizedEmptyMessage(languageCode: String): String =
    if (languageCode.substringBefore('-').equals("es", ignoreCase = true))
        "Las canciones que identifiques con AURIA aparecerán aquí."
    else "Songs identified by AURIA will appear here."

private fun recognizedSearchDescription(languageCode: String): String =
    if (languageCode.substringBefore('-').equals("es", ignoreCase = true)) "Buscar canción" else "Search song"

internal fun LazyListScope.recognizedLibrarySection(
    entries: List<RecognitionHistoryEntry>,
    query: String,
    languageCode: String,
    onOpen: (RecognitionHistoryEntry) -> Unit,
    onSearch: (RecognitionHistoryEntry) -> Unit
) {
    val visible = if (query.isBlank()) entries else entries.filter { entry ->
        entry.result.title.contains(query, ignoreCase = true) ||
            entry.result.artist.contains(query, ignoreCase = true) ||
            entry.result.album.contains(query, ignoreCase = true)
    }
    if (visible.isEmpty()) {
        item(key = "recognized-empty") {
            Text(
                if (entries.isEmpty()) recognizedEmptyMessage(languageCode)
                else if (languageCode.substringBefore('-').equals("es", ignoreCase = true))
                    "No hay coincidencias." else "No matches.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth().padding(18.dp)
            )
        }
    } else {
        items(visible, key = { "recognized-${it.id}" }) { entry ->
            RecognizedLibraryRow(
                entry = entry,
                languageCode = languageCode,
                onOpen = { onOpen(entry) },
                onSearch = { onSearch(entry) }
            )
        }
    }
}

@Composable
private fun RecognizedLibraryRow(
    entry: RecognitionHistoryEntry,
    languageCode: String,
    onOpen: () -> Unit,
    onSearch: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        color = colors.surfaceContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp))
                    .background(colors.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = colors.onSecondaryContainer)
                if (entry.result.artworkUrl.isNotBlank()) {
                    AsyncImage(
                        model = entry.result.artworkUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.result.title, color = colors.onSurface,
                    fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(entry.result.artist, color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall, maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onSearch) {
                Icon(Icons.Rounded.Search,
                    contentDescription = recognizedSearchDescription(languageCode),
                    tint = colors.primary)
            }
        }
    }
}
