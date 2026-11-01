package com.luc4n3x.levyra.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.luc4n3x.levyra.domain.Track
import com.luc4n3x.levyra.ui.i18n.LocalLevyraStrings
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode
import com.luc4n3x.levyra.viewmodel.HomeRenderSnapshot
import com.luc4n3x.levyra.viewmodel.HomeViewModel

private class AuriaHomeWords(es: Boolean) {
    val statement = if (es) "TU MÚSICA. TU UNIVERSO." else "YOUR MUSIC. YOUR UNIVERSE."
    val play = if (es) "ESCUCHAR" else "LISTEN"
    val live = if (es) "REPRODUCIENDO" else "NOW PLAYING"
    val discover = if (es) "Descubrir" else "Discover"
    val find = if (es) "Buscar" else "Search"
    val collection = if (es) "Mi música" else "My music"
    val fresh = if (es) "Radar musical" else "Music radar"
    val personal = if (es) "Señales para ti" else "Signals for you"
    val archives = if (es) "Tu archivo" else "Your archive"
    val favorites = if (es) "Favoritos" else "Favorites"
    val recent = if (es) "Recientes" else "Recently played"
    val tracks = if (es) "canciones" else "tracks"
    val identity = if (es) "AURIA / SISTEMA SONORO" else "AURIA / SOUND SYSTEM"
    val fallback = if (es) "Encuentra algo nuevo" else "Find something new"
    val identify = if (es) "IDENTIFICAR MÚSICA" else "IDENTIFY MUSIC"
    val identifySub = if (es) "Reconoce música de otras aplicaciones" else "Recognize music from other apps"
}

/**
 * AURIA V4 is a genuinely new destination composed around Listening, Radar
 * and Archive. It deliberately does not reuse Levyra's HomeStudio sections.
 * Existing data, queues, playback, account state and local files are untouched.
 */
@Composable
internal fun AuriaIndependentHome(
    viewModel: HomeViewModel,
    snapshot: HomeRenderSnapshot,
    listState: LazyListState,
    mode: AuriaVisualMode,
    onSearch: () -> Unit,
    onExplore: () -> Unit,
    onLibrary: () -> Unit,
    onOpenPlayer: () -> Unit,
    onRefresh: () -> Unit
) {
    val state = snapshot.state
    val colors = MaterialTheme.colorScheme
    val language = LocalLevyraStrings.current.code
    val words = remember(language) { AuriaHomeWords(language.startsWith("es")) }
    val personal = remember(state.personalOrbitTracks, state.recentListens, state.favorites, state.tracks) {
        (state.personalOrbitTracks + state.recentListens + state.favorites + state.tracks)
            .distinctBy { it.id }.take(24)
    }
    val radar = remember(state.charts, state.homeSections, state.tracks) {
        (state.charts + state.homeSections.flatMap { it.tracks } + state.tracks)
            .distinctBy { it.id }.take(18)
    }
    val lead = state.currentTrack ?: personal.firstOrNull() ?: radar.firstOrNull()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 52.dp, bottom = 210.dp),
        verticalArrangement = Arrangement.spacedBy(if (mode == AuriaVisualMode.Pulse) 23.dp else 31.dp)
    ) {
        item(key = "auria-v4-header") {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuriaHaloEmblem(Modifier.size(42.dp), colors.primary, colors.tertiary)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(words.identity, color = colors.primary, letterSpacing = 1.4.sp,
                        fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        state.userName.takeIf { it.isNotBlank() } ?: "AURIA",
                        color = colors.onSurface, fontWeight = FontWeight.Bold,
                        fontSize = 22.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Actualizar", tint = colors.onSurfaceVariant)
                }
                IconButton(onClick = viewModel::openSettings) {
                    AuriaHaloEmblem(Modifier.size(28.dp), colors.primary, colors.tertiary)
                }
            }
        }
        item(key = "auria-v4-statement") {
            Column(Modifier.padding(horizontal = 23.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("01 / ${mode.name.uppercase()}", color = colors.tertiary,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text(words.statement, color = colors.onSurface, fontSize = if (mode == AuriaVisualMode.Pulse) 28.sp else 34.sp,
                    lineHeight = if (mode == AuriaVisualMode.Pulse) 31.sp else 37.sp,
                    fontWeight = FontWeight.Black, maxLines = 2)
            }
        }
        item(key = "auria-v4-listening") {
            AuriaListeningStage(
                track = lead,
                mode = mode,
                words = words,
                playing = state.isPlaying && state.currentTrack != null,
                onOpen = { if (state.currentTrack != null) onOpenPlayer() else lead?.let { viewModel.playFrom(personal.ifEmpty { radar }, it) } }
            )
        }
        item(key = "auria-v4-navigation") {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AuriaDestinationCard(words.find, "02", Modifier.weight(1f), colors.primary, onSearch)
                AuriaDestinationCard(words.discover, "03", Modifier.weight(1f), colors.tertiary, onExplore)
                AuriaDestinationCard(words.collection, "04", Modifier.weight(1f), colors.primary, onLibrary)
            }
        }
        item(key = "auria-v4-identify", contentType = "auria-identify") {
            val ink = MaterialTheme.colorScheme
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 5.dp))
                    .background(ink.secondaryContainer)
                    .clickable(role = Role.Button, onClick = viewModel::openRecognition)
                    .padding(horizontal = 17.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AuriaHaloEmblem(Modifier.size(37.dp), ink.primary, ink.tertiary)
                Column(Modifier.weight(1f)) {
                    Text(words.identify, fontWeight = FontWeight.Black,
                        color = ink.onSecondaryContainer, fontSize = 14.sp)
                    Text(words.identifySub, color = ink.onSecondaryContainer.copy(alpha = .8f),
                        fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Icon(Icons.Rounded.ArrowForward, contentDescription = null,
                    tint = ink.onSecondaryContainer, modifier = Modifier.size(20.dp))
            }
        }
        if (personal.isNotEmpty()) {
            item(key = "auria-v4-personal-title") {
                AuriaEditorialHeading("02", words.personal, personal.size, Modifier.padding(horizontal = 22.dp))
            }
            item(key = "auria-v4-personal-tracks") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    items(personal.take(12), key = { "personal_${it.id}" }) { track ->
                        AuriaSignalTile(track, mode) { viewModel.playFrom(personal, track) }
                    }
                }
            }
        }
        if (radar.isNotEmpty()) {
            item(key = "auria-v4-radar-title") {
                AuriaEditorialHeading("03", words.fresh, radar.size, Modifier.padding(horizontal = 22.dp))
            }
            items(radar.take(if (mode == AuriaVisualMode.Pulse) 5 else 4), key = { "radar_${it.id}" }) { track ->
                AuriaRadarRow(track, radar.indexOf(track), mode, Modifier.padding(horizontal = 20.dp)) {
                    viewModel.playFrom(radar, track)
                }
            }
        }
        item(key = "auria-v4-archive") {
            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                AuriaEditorialHeading("04", words.archives, null)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AuriaArchiveAction(
                        title = words.favorites,
                        detail = "${state.favorites.size} ${words.tracks}",
                        Modifier.weight(1f),
                        onLibrary
                    )
                    AuriaArchiveAction(
                        title = words.recent,
                        detail = "${state.recentListens.size} ${words.tracks}",
                        Modifier.weight(1f),
                        onLibrary
                    )
                }
            }
        }
    }
}

@Composable
private fun AuriaListeningStage(
    track: Track?,
    mode: AuriaVisualMode,
    words: AuriaHomeWords,
    playing: Boolean,
    onOpen: () -> Unit
) {
    val palette = MaterialTheme.colorScheme
    val cut = when (mode) {
        AuriaVisualMode.Flow -> RoundedCornerShape(topStart = 10.dp, topEnd = 58.dp, bottomStart = 58.dp, bottomEnd = 10.dp)
        AuriaVisualMode.Nebula -> RoundedCornerShape(42.dp)
        AuriaVisualMode.Pulse -> RoundedCornerShape(14.dp)
        AuriaVisualMode.Studio -> RoundedCornerShape(topStart = 2.dp, topEnd = 30.dp, bottomStart = 2.dp, bottomEnd = 30.dp)
    }
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .height(if (mode == AuriaVisualMode.Pulse) 214.dp else 284.dp)
            .clip(cut)
            .background(
                Brush.linearGradient(
                    listOf(palette.surfaceContainerHigh, palette.surfaceContainerLowest, palette.primaryContainer)
                )
            )
            .border(1.dp, palette.outlineVariant, cut)
            .clickable(role = Role.Button, onClick = onOpen)
    ) {
        val artSize = if (mode == AuriaVisualMode.Pulse) 145.dp else 212.dp
        Box(
            Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                .size(artSize)
                .clip(
                    if (mode == AuriaVisualMode.Nebula) CircleShape
                    else RoundedCornerShape(topStart = 84.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 84.dp)
                )
                .background(palette.primaryContainer)
        ) {
            if (track != null) {
                AsyncImage(
                    model = track.largeThumbnailUrl.ifBlank { track.thumbnailUrl },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Box(Modifier.fillMaxSize().background(
            Brush.horizontalGradient(
                listOf(palette.surface.copy(alpha = .97f), palette.surface.copy(alpha = .80f),
                    palette.surface.copy(alpha = .12f))
            )
        ))
        Column(
            modifier = Modifier.align(Alignment.CenterStart)
                .fillMaxWidth(.72f).padding(start = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).background(if (playing) palette.primary else palette.tertiary, CircleShape))
                Spacer(Modifier.width(7.dp))
                Text(words.live.uppercase(), color = palette.primary,
                    letterSpacing = 1.4.sp, fontWeight = FontWeight.Black, fontSize = 10.sp)
            }
            Text(track?.title ?: words.fallback,
                color = palette.onSurface, fontSize = if (mode == AuriaVisualMode.Pulse) 22.sp else 29.sp,
                lineHeight = if (mode == AuriaVisualMode.Pulse) 26.sp else 32.sp,
                fontWeight = FontWeight.Black, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Text(track?.artist.orEmpty(), color = palette.onSurfaceVariant,
                fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(
                modifier = Modifier.clip(RoundedCornerShape(7.dp))
                    .background(palette.primary).padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Text(words.play, color = palette.onPrimary, fontWeight = FontWeight.Black,
                    fontSize = 10.sp, letterSpacing = 1.1.sp)
                Icon(Icons.Rounded.ArrowForward, contentDescription = null,
                    tint = palette.onPrimary, modifier = Modifier.size(15.dp))
            }
        }
    }
}

@Composable
private fun AuriaDestinationCard(label: String, index: String, modifier: Modifier, color: Color, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier.height(90.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 23.dp, bottomStart = 23.dp, bottomEnd = 8.dp))
            .background(colors.surfaceContainer)
            .border(1.dp, colors.outlineVariant.copy(alpha = .55f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick).padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(index, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = colors.onSurface, fontSize = 13.sp,
                fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ArrowForward, null, tint = color, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun AuriaEditorialHeading(index: String, title: String, count: Int?, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(index, color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Black)
        Box(Modifier.width(2.dp).height(23.dp).background(colors.primary))
        Text(title, color = colors.onSurface, fontWeight = FontWeight.Black, fontSize = 22.sp, modifier = Modifier.weight(1f))
        if (count != null) Text(count.toString().padStart(2, '0'), color = colors.onSurfaceVariant,
            fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AuriaSignalTile(track: Track, mode: AuriaVisualMode, onPlay: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val width = if (mode == AuriaVisualMode.Pulse) 134.dp else 164.dp
    Column(
        Modifier.width(width).clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceContainer).clickable(onClick = onPlay),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(if (mode == AuriaVisualMode.Pulse) 130.dp else 164.dp)
            .background(colors.surfaceContainerHighest)) {
            AsyncImage(
                model = track.largeThumbnailUrl.ifBlank { track.thumbnailUrl },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(Modifier.align(Alignment.BottomEnd).padding(8.dp).size(34.dp)
                .background(colors.primary, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.ArrowForward, contentDescription = null,
                    tint = colors.onPrimary, modifier = Modifier.size(18.dp))
            }
        }
        Text(track.title, modifier = Modifier.padding(horizontal = 10.dp), color = colors.onSurface,
            fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(track.artist, modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 12.dp),
            color = colors.onSurfaceVariant, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun AuriaRadarRow(track: Track, position: Int, mode: AuriaVisualMode, modifier: Modifier, onPlay: () -> Unit) {
    val palette = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth().height(if (mode == AuriaVisualMode.Studio) 65.dp else 77.dp)
            .clip(RoundedCornerShape(if (mode == AuriaVisualMode.Pulse) 8.dp else 16.dp))
            .background(palette.surfaceContainer)
            .clickable(onClick = onPlay)
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Text((position + 1).toString().padStart(2,'0'), color = palette.primary, fontSize = 13.sp,
            fontWeight = FontWeight.Black)
        AsyncImage(
            model = track.thumbnailUrl.ifBlank { track.largeThumbnailUrl },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(7.dp))
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(track.title, color = palette.onSurface, fontWeight = FontWeight.Bold,
                fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, color = palette.onSurfaceVariant, fontSize = 11.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Icon(Icons.Rounded.ArrowForward, contentDescription = null,
            tint = palette.primary, modifier = Modifier.size(21.dp))
    }
}

@Composable
private fun AuriaArchiveAction(title: String, detail: String, modifier: Modifier, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier.height(106.dp)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 24.dp))
            .background(colors.surfaceContainerHigh).clickable(onClick = onClick).padding(17.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = colors.onSurface, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(detail, color = colors.onSurfaceVariant, fontSize = 11.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ArrowForward, null, tint = colors.primary, modifier = Modifier.size(18.dp))
        }
    }
}
