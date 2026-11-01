package com.luc4n3x.levyra.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import coil3.compose.AsyncImage
import com.luc4n3x.levyra.domain.Track
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

/**
 * AURIA's independent home composition. Uses existing libraries and playback
 * callbacks without migrating playlists, metadata or local storage.
 */
@Composable
internal fun AuriaUniverseHome(
    mode: AuriaVisualMode,
    userName: String,
    currentTrack: Track?,
    isPlaying: Boolean,
    recentTracks: List<Track>,
    personalTracks: List<Track>,
    favoriteTracks: List<Track>,
    recommendations: List<Track>,
    listState: LazyListState,
    onSearch: () -> Unit,
    onSettings: () -> Unit,
    onIdentify: () -> Unit,
    onDiscover: () -> Unit,
    onLibrary: () -> Unit,
    onPlay: (Track) -> Unit,
    onOpenPlayer: () -> Unit,
    onTrackActions: (Track) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val suggested = (recentTracks + personalTracks + recommendations + favoriteTracks)
        .distinctBy { it.id }
    val focus = currentTrack ?: suggested.firstOrNull()
    val latest = recentTracks.distinctBy { it.id }.take(8)
    val personal = personalTracks.distinctBy { it.id }.take(9)
    val favorites = favoriteTracks.distinctBy { it.id }.take(7)
    val newMusic = recommendations.distinctBy { it.id }.take(7)
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().background(colors.background),
        contentPadding = PaddingValues(top = top + 14.dp, bottom = 200.dp),
        verticalArrangement = Arrangement.spacedBy(if (mode == AuriaVisualMode.Pulse) 17.dp else 24.dp)
    ) {
        item(key = "auria-header", contentType = "auria-header") {
            AuriaHomeMasthead(
                mode = mode,
                userName = userName,
                onSearch = onSearch,
                onSettings = onSettings
            )
        }
        item(key = "auria-focus", contentType = "auria-focus") {
            AuriaFocusStage(
                mode = mode,
                track = focus,
                isCurrent = focus != null && focus.id == currentTrack?.id,
                isPlaying = isPlaying,
                onOpen = { if (focus != null) {
                    if (focus.id == currentTrack?.id) onOpenPlayer() else onPlay(focus)
                } else onDiscover() }
            )
        }
        item(key = "auria-navigation", contentType = "auria-navigation") {
            AuriaHomeActions(
                mode = mode,
                onIdentify = onIdentify,
                onSearch = onSearch,
                onDiscover = onDiscover,
                onLibrary = onLibrary
            )
        }
        if (latest.isNotEmpty()) {
            item(key = "auria-recent-heading") {
                AuriaHomeSectionHeading("SIGUE ESCUCHANDO", "Tus últimas canciones", mode)
            }
            items(latest, key = { "recent-" + it.id }, contentType = { "auria-recent" }) { track ->
                AuriaHomeTrackLine(track, active = track.id == currentTrack?.id, mode = mode,
                    onPlay = { onPlay(track) }, onActions = { onTrackActions(track) })
            }
        }
        if (personal.isNotEmpty()) {
            item(key = "auria-universe-title") {
                AuriaHomeSectionHeading("TU UNIVERSO", "Seleccionado para ti", mode)
            }
            item(key = "auria-universe-gallery") {
                AuriaHomeGallery(personal, mode, onPlay)
            }
        }
        if (favorites.isNotEmpty()) {
            item(key = "auria-likes-title") {
                AuriaHomeSectionHeading("EN TU ÓRBITA", "Lo que más te gusta", mode)
            }
            items(favorites, key = { "liked-" + it.id }, contentType = { "auria-liked" }) { track ->
                AuriaHomeTrackLine(track, active = track.id == currentTrack?.id, mode = mode,
                    onPlay = { onPlay(track) }, onActions = { onTrackActions(track) })
            }
        }
        if (newMusic.isNotEmpty()) {
            item(key = "auria-discover-heading") {
                AuriaHomeSectionHeading("FUERA DE TU ÓRBITA", "Algo diferente para escuchar", mode)
            }
            item(key = "auria-discover-shelf") {
                AuriaHomeGallery(newMusic, mode, onPlay)
            }
        }
        item(key = "auria-end", contentType = "auria-end") {
            Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AuriaHaloEmblem(Modifier.size(27.dp), colors.primary, colors.tertiary)
                Text("AURIA  /  TU SONIDO NO TIENE LÍMITES",
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun AuriaHomeMasthead(
    mode: AuriaVisualMode,
    userName: String,
    onSearch: () -> Unit,
    onSettings: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AuriaHaloEmblem(Modifier.size(43.dp), colors.primary, colors.tertiary)
        Column(Modifier.weight(1f)) {
            Text("A U R I A   /   " + mode.name.uppercase(),
                color = colors.primary, style = MaterialTheme.typography.labelMedium)
            Text(
                if (userName.isBlank()) "Tu universo musical" else "Tu universo, " + userName,
                color = colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(onClick = onSearch) {
            Icon(Icons.Rounded.Search, contentDescription = "Buscar", tint = colors.onSurface)
        }
        IconButton(onClick = onSettings) {
            Icon(Icons.Rounded.Settings, contentDescription = "Ajustes", tint = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun AuriaFocusStage(
    mode: AuriaVisualMode,
    track: Track?,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onOpen: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val accent = colors.primary
    val shape = when (mode) {
        AuriaVisualMode.Flow -> RoundedCornerShape(topStart = 14.dp, topEnd = 52.dp, bottomStart = 52.dp, bottomEnd = 14.dp)
        AuriaVisualMode.Nebula -> RoundedCornerShape(46.dp)
        AuriaVisualMode.Pulse -> RoundedCornerShape(14.dp)
        AuriaVisualMode.Studio -> RoundedCornerShape(6.dp)
    }
    val height = when (mode) {
        AuriaVisualMode.Flow -> 258.dp
        AuriaVisualMode.Nebula -> 288.dp
        AuriaVisualMode.Pulse -> 174.dp
        AuriaVisualMode.Studio -> 208.dp
    }
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
            .height(height).clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(colors.surfaceContainerHigh, colors.surfaceContainer, colors.secondaryContainer)
                )
            )
            .clickable(role = Role.Button, onClickLabel = "Escuchar o abrir reproductor", onClick = onOpen)
    ) {
        val narrow = maxWidth < 335.dp
        val artSize = when (mode) {
            AuriaVisualMode.Pulse -> if (narrow) 90.dp else 112.dp
            AuriaVisualMode.Studio -> if (narrow) 100.dp else 125.dp
            AuriaVisualMode.Nebula -> 176.dp
            AuriaVisualMode.Flow -> if (narrow) 118.dp else 145.dp
        }
        val image = track?.largeThumbnailUrl?.ifBlank { track.thumbnailUrl }.orEmpty()
        if (mode == AuriaVisualMode.Nebula) {
            Canvas(Modifier.fillMaxSize()) {
                val p = Offset(size.width * .68f, size.height * .44f)
                drawCircle(accent.copy(alpha = .3f), size.width * .34f, p, style = Stroke(3.dp.toPx()))
                drawCircle(colors.tertiary.copy(alpha = .3f), size.width * .43f, p, style = Stroke(1.dp.toPx()))
            }
        }
        val stageArt: @Composable () -> Unit = {
            Box(
                Modifier.size(artSize)
                    .clip(if (mode == AuriaVisualMode.Nebula) CircleShape else RoundedCornerShape(20.dp))
                    .background(colors.surface),
                contentAlignment = Alignment.Center
            ) {
                if (image.isNotBlank()) AsyncImage(image, contentDescription = track?.title,
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                else Icon(Icons.Rounded.GraphicEq, contentDescription = null,
                    modifier = Modifier.size(45.dp), tint = accent)
            }
        }
        Column(Modifier.align(Alignment.TopStart).padding(23.dp)) {
            Text(
                when {
                    isCurrent && isPlaying -> "●  EN REPRODUCCIÓN"
                    isCurrent -> "◉  EN PAUSA"
                    else -> "✦  ESTACIÓN PERSONAL"
                },
                color = colors.primary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            Text(
                when (mode) {
                    AuriaVisualMode.Flow -> "SIGUIENTE\nFRECUENCIA"
                    AuriaVisualMode.Nebula -> "EN TU\nUNIVERSO"
                    AuriaVisualMode.Pulse -> "AL AIRE"
                    AuriaVisualMode.Studio -> "SEÑAL / 01"
                },
                color = colors.onSurface.copy(alpha = .55f),
                lineHeight = 21.sp,
                fontSize = if (mode == AuriaVisualMode.Pulse) 17.sp else 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        when (mode) {
            AuriaVisualMode.Flow, AuriaVisualMode.Pulse, AuriaVisualMode.Studio -> {
                Row(
                    Modifier.align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(track?.title ?: "Descubre tu próxima canción",
                            color = colors.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = if (mode == AuriaVisualMode.Pulse) 1 else 2,
                            overflow = TextOverflow.Ellipsis)
                        Text(track?.artist ?: "Explora tu música",
                            color = colors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                        AuriaSignalRibbon(Modifier.fillMaxWidth(.9f), colors.primary, colors.tertiary)
                    }
                    stageArt()
                }
            }
            AuriaVisualMode.Nebula -> {
                Box(Modifier.align(Alignment.CenterEnd).padding(end = 23.dp, top = 15.dp)) { stageArt() }
                Column(Modifier.align(Alignment.BottomStart).padding(23.dp).fillMaxWidth(.68f)) {
                    Text(track?.title ?: "Explora el universo",
                        color = colors.onSurface, style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(track?.artist ?: "AURIA",
                        color = colors.onSurfaceVariant,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        Box(Modifier.align(Alignment.TopEnd).padding(15.dp)
            .size(42.dp).clip(CircleShape).background(accent),
            contentAlignment = Alignment.Center) {
            Icon(if (isCurrent && isPlaying) Icons.Rounded.GraphicEq else Icons.Rounded.PlayArrow,
                contentDescription = null, tint = colors.onPrimary)
        }
    }
}

@Composable
private fun AuriaHomeActions(
    mode: AuriaVisualMode,
    onIdentify: () -> Unit,
    onSearch: () -> Unit,
    onDiscover: () -> Unit,
    onLibrary: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Text("CENTRO DE CONTROL", style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant, letterSpacing = 2.sp)
            Spacer(Modifier.weight(1f))
            Text(mode.name.uppercase(), color = colors.primary, style = MaterialTheme.typography.labelSmall)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AuriaQuickAction("IDENTIFICAR", "Escucha lo que suena", "◎", onIdentify, Modifier.weight(1f))
            AuriaQuickAction("DESCUBRIR", "Fuera de tu órbita", "✧", onDiscover, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AuriaQuickAction("MI MÚSICA", "Todo en un lugar", "▤", onLibrary, Modifier.weight(1f))
            AuriaQuickAction("BUSCAR", "Encuentra ese tema", "⌕", onSearch, Modifier.weight(1f))
        }
    }
}

@Composable
private fun AuriaQuickAction(title: String, subtitle: String, symbol: String, click: () -> Unit, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier.height(86.dp).clip(RoundedCornerShape(15.dp))
            .background(colors.surfaceContainerLow)
            .clickable(role = Role.Button, onClick = click)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Text(symbol, color = colors.primary, fontSize = 28.sp, fontWeight = FontWeight.Light)
        Column(Modifier.weight(1f)) {
            Text(title, color = colors.onSurface, style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, color = colors.onSurfaceVariant, style = MaterialTheme.typography.labelSmall,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun AuriaHomeSectionHeading(tag: String, subtitle: String, mode: AuriaVisualMode) {
    val colors = MaterialTheme.colorScheme
    Column(Modifier.padding(horizontal = 21.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(width = 5.dp, height = 27.dp).background(colors.primary, RoundedCornerShape(3.dp)))
            Text(tag, color = colors.onSurface, fontWeight = FontWeight.Black,
                fontSize = if (mode == AuriaVisualMode.Pulse) 19.sp else 22.sp,
                letterSpacing = -.4.sp)
        }
        Text(subtitle, color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AuriaHomeTrackLine(
    track: Track,
    active: Boolean,
    mode: AuriaVisualMode,
    onPlay: () -> Unit,
    onActions: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(if (mode == AuriaVisualMode.Studio) 5.dp else 14.dp))
            .background(if (active) colors.primaryContainer else colors.surfaceContainerLow)
            .clickable(role = Role.Button, onClick = onPlay)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AuriaTrackImage(track, Modifier.size(if (mode == AuriaVisualMode.Pulse) 48.dp else 56.dp))
        Column(Modifier.weight(1f)) {
            Text(track.title, color = colors.onSurface, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(if (active) "●" else "↗", color = colors.primary, fontSize = 19.sp)
        IconButton(onClick = onActions, modifier = Modifier.size(44.dp)) {
            Icon(Icons.Rounded.ArrowForward, contentDescription = "Opciones de " + track.title,
                tint = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun AuriaHomeGallery(tracks: List<Track>, mode: AuriaVisualMode, onPlay: (Track) -> Unit) {
    val columns = if (mode == AuriaVisualMode.Pulse || mode == AuriaVisualMode.Studio) 2 else 3
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        tracks.chunked(columns).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { track ->
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                            .clickable(role = Role.Button, onClick = { onPlay(track) }),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        AuriaTrackImage(track, Modifier.fillMaxWidth().height(if (columns == 2) 150.dp else 108.dp))
                        Text(track.title, maxLines = 1, overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 3.dp))
                        Text(track.artist, maxLines = 1, overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 3.dp))
                    }
                }
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun AuriaTrackImage(track: Track, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val url = track.largeThumbnailUrl.ifBlank { track.thumbnailUrl }
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(colors.surfaceContainerHigh),
        contentAlignment = Alignment.Center) {
        if (url.isNotBlank()) AsyncImage(model = url, contentDescription = null,
            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else Icon(Icons.Rounded.MusicNote, contentDescription = null,
            tint = colors.primary, modifier = Modifier.size(30.dp))
    }
}
