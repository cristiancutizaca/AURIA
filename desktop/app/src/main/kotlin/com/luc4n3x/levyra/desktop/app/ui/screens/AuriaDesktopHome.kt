package com.luc4n3x.levyra.desktop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luc4n3x.levyra.desktop.app.state.DiscoverUiState
import com.luc4n3x.levyra.desktop.app.ui.components.Artwork
import com.luc4n3x.levyra.desktop.app.ui.components.AuriaHaloMark
import com.luc4n3x.levyra.desktop.app.ui.components.ScrollableColumn
import com.luc4n3x.levyra.desktop.app.ui.components.TrackActions
import com.luc4n3x.levyra.desktop.app.ui.i18n.LocalStrings
import com.luc4n3x.levyra.desktop.core.model.Track
import com.luc4n3x.levyra.desktop.core.storage.LibraryData

/**
 * Independent AURIA desktop home. Not the Levyra hero + quick-access card
 * sequence. Its editorial program mirrors Android: Listening, Signals, Vault.
 * Uses existing playback and catalog callbacks, never mutating persistence.
 */
@Composable
fun AuriaDesktopHome(
    library: LibraryData,
    discover: DiscoverUiState,
    currentTrack: Track?,
    actions: TrackActions,
    onOpenSearch: () -> Unit,
    onOpenDiscover: () -> Unit,
    onOpenNewReleases: () -> Unit,
    onPlayMix: (List<Track>) -> Unit,
    onOpenPlaylist: (String) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onImportUrl: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val history = remember(library.history) {
        library.history.sortedByDescending { it.playedAt }.map { it.track }.distinctBy { it.id }
    }
    val primary = remember(currentTrack, history, library.favorites, discover.tracks) {
        (listOfNotNull(currentTrack) + history + library.favorites + discover.tracks)
            .filter { it.title.isNotBlank() }
            .distinctBy { it.id }
            .take(24)
    }
    val featured = primary.firstOrNull()
    ScrollableColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 36.dp, vertical = 27.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item(key = "auria-desktop-head") {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                AuriaHaloMark(Modifier.size(53.dp), colors.primary)
                Column(Modifier.weight(1f)) {
                    Text("AURIA  /  01   SOUND SYSTEM", color = colors.primary,
                        fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.6.sp)
                    Text("TU MÚSICA. TU UNIVERSO.", color = colors.onSurface,
                        fontSize = 34.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        item(key = "auria-desktop-stage") {
            Row(
                modifier = Modifier.fillMaxWidth().height(285.dp)
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 76.dp, bottomEnd = 10.dp, bottomStart = 76.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(colors.primaryContainer, colors.surfaceContainerHigh, colors.surfaceContainerLow)
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f).padding(27.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("01 / ESCUCHANDO AHORA", color = colors.primary,
                        fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.7.sp)
                    Text(featured?.title ?: "ENCUENTRA TU PRÓXIMA CANCIÓN",
                        color = colors.onSurface, fontWeight = FontWeight.Black,
                        maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 33.sp)
                    Text(featured?.artist.orEmpty(), color = colors.onSurfaceVariant,
                        fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(
                        Modifier.clip(RoundedCornerShape(9.dp)).background(colors.primary)
                            .clickable { if (primary.isNotEmpty()) actions.onPlay(primary, 0) else onOpenSearch() }
                            .padding(horizontal = 20.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("ESCUCHAR", color = colors.onPrimary, fontSize = 12.sp,
                            fontWeight = FontWeight.Black)
                        Text("↗", color = colors.onPrimary, fontSize = 14.sp)
                    }
                }
                Box(
                    modifier = Modifier.width(260.dp).height(260.dp).padding(end = 16.dp)
                        .clip(RoundedCornerShape(topStart = 90.dp, topEnd = 12.dp, bottomEnd = 90.dp, bottomStart = 12.dp))
                        .background(colors.surfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    Artwork(featured?.artworkUrl.orEmpty(), modifier = Modifier.size(260.dp), cornerRadius = 10.dp)
                }
            }
        }
        item(key = "auria-desktop-destinations") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                AuriaDesktopCommandCard("02", "BUSCAR", "Encuentra cualquier pista", Modifier.weight(1f), onOpenSearch)
                AuriaDesktopCommandCard("03", "EXPLORAR", "Nuevas frecuencias", Modifier.weight(1f), onOpenDiscover)
                AuriaDesktopCommandCard("04", "NOVEDADES", "Últimos lanzamientos", Modifier.weight(1f), onOpenNewReleases)
                AuriaDesktopCommandCard("05", "MIX", "Mezcla personal", Modifier.weight(1f)) {
                    if (primary.isNotEmpty()) onPlayMix(primary)
                }
            }
        }
        if (primary.isNotEmpty()) {
            item(key = "auria-desktop-signals-title") {
                AuriaDesktopSectionHeading("02", "SEÑALES PARA TI", primary.size)
            }
            item(key = "auria-desktop-signals") {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    itemsIndexed(primary.take(12), key = { _, track -> track.id }) { index, track ->
                        Column(
                            Modifier.width(155.dp).clip(RoundedCornerShape(10.dp))
                                .background(colors.surfaceContainer)
                                .clickable { actions.onPlay(primary, index) }
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Artwork(track.artworkUrl, modifier = Modifier.size(135.dp), cornerRadius = 6.dp)
                            Text(track.title, color = colors.onSurface, fontSize = 13.sp,
                                fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(track.artist, color = colors.onSurfaceVariant, fontSize = 11.sp,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
        if (history.isNotEmpty()) {
            item(key = "auria-desktop-history-title") {
                AuriaDesktopSectionHeading("03", "TU HISTORIAL", history.size)
            }
            items(history.take(6).size, key = { "history_" + it }) { i ->
                val track = history[i]
                Row(
                    Modifier.fillMaxWidth().height(57.dp).clip(RoundedCornerShape(8.dp))
                        .background(colors.surfaceContainer)
                        .clickable { actions.onPlay(history, i) }
                        .padding(horizontal = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text((i + 1).toString().padStart(2, '0'), color = colors.primary,
                        fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Artwork(track.artworkUrl, Modifier.size(42.dp), cornerRadius = 5.dp)
                    Text(track.title, color = colors.onSurface, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(track.artist, color = colors.onSurfaceVariant, fontSize = 12.sp,
                        modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("▶", color = colors.primary)
                }
            }
        }
    }
}

@Composable
private fun AuriaDesktopCommandCard(index: String, title: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier.height(100.dp).clip(RoundedCornerShape(topStart = 5.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 5.dp))
            .background(colors.surfaceContainerHigh).clickable(onClick = onClick).padding(15.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(index, color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Black)
        Text(title, color = colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Black)
        Text(subtitle, color = colors.onSurfaceVariant, fontSize = 11.sp,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun AuriaDesktopSectionHeading(number: String, label: String, count: Int) {
    val colors = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(number, color = colors.primary, fontWeight = FontWeight.Black, fontSize = 12.sp)
        Box(Modifier.width(2.dp).height(27.dp).background(colors.primary))
        Text(label, color = colors.onSurface, fontSize = 22.sp,
            fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
        Text(count.toString().padStart(2, '0'), color = colors.onSurfaceVariant, fontSize = 12.sp)
    }
}
