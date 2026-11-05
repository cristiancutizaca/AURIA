package com.luc4n3x.levyra.ui.library

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.luc4n3x.levyra.feature.recognition.RecognitionHistoryEntry
import com.luc4n3x.levyra.ui.i18n.LocalLevyraStrings

/**
 * Smart library folder backed by the existing Room recognition history.
 * Contains identification metadata, not downloaded audio files.
 */
@Composable
internal fun RecognitionLibraryShortcut(
    count: Int,
    onOpen: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val spanish = LocalLevyraStrings.current.code.startsWith("es", ignoreCase = true)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.primaryContainer)
            .clickable(onClick = onOpen)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Folder,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = colors.onPrimaryContainer
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (spanish) "Música identificada" else "Identified music",
                color = colors.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = if (spanish) "$count canciones reconocidas" else "$count recognized songs",
                color = colors.onPrimaryContainer.copy(alpha = 0.75f),
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = null,
            tint = colors.onPrimaryContainer
        )
    }
}

@Composable
internal fun RecognitionLibraryFolder(
    history: List<RecognitionHistoryEntry>,
    onOpenEntry: (RecognitionHistoryEntry) -> Unit,
    onDeleteEntry: (String) -> Unit,
    onClose: () -> Unit
) {
    BackHandler(onBack = onClose)
    val colors = MaterialTheme.colorScheme
    val spanish = LocalLevyraStrings.current.code.startsWith("es", ignoreCase = true)
    var query by rememberSaveable { mutableStateOf("") }
    val unique = remember(history) {
        history.sortedByDescending { it.recognizedAt }.distinctBy { it.identityKey }
    }
    val filtered = remember(unique, query) {
        if (query.isBlank()) unique else unique.filter { entry ->
            entry.result.title.contains(query, ignoreCase = true) ||
                entry.result.artist.contains(query, ignoreCase = true) ||
                entry.result.album.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = if (spanish) "Volver" else "Back")
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = if (spanish) "Música identificada" else "Identified music",
                    color = colors.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = if (spanish) "${unique.size} canciones" else "${unique.size} songs",
                    color = colors.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            placeholder = { Text(if (spanish) "Buscar entre identificadas" else "Search identified songs") }
        )

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (history.isEmpty()) {
                        if (spanish) "Las canciones que identifiques aparecerán aquí."
                        else "Songs you identify will appear here."
                    } else {
                        if (spanish) "No se encontraron canciones." else "No songs found."
                    },
                    color = colors.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 14.dp, bottom = 150.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.id }) { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.surfaceContainer)
                            .clickable { onOpenEntry(entry) }
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(colors.surfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            if (entry.result.artworkUrl.isBlank()) {
                                Icon(Icons.Rounded.MusicNote, contentDescription = null)
                            } else {
                                AsyncImage(
                                    model = entry.result.artworkUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                entry.result.title,
                                color = colors.onSurface,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                entry.result.artist,
                                color = colors.onSurfaceVariant,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = { onOpenEntry(entry) }) {
                            Icon(
                                Icons.Rounded.PlayArrow,
                                contentDescription = if (spanish) "Abrir canción" else "Open song"
                            )
                        }
                        IconButton(onClick = { onDeleteEntry(entry.id) }) {
                            Icon(
                                Icons.Rounded.DeleteOutline,
                                contentDescription = if (spanish) "Quitar canción" else "Remove song",
                                tint = colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
