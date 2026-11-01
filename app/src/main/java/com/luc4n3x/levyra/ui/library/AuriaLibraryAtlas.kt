package com.luc4n3x.levyra.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.luc4n3x.levyra.domain.Track
import com.luc4n3x.levyra.ui.AuriaHaloEmblem
import com.luc4n3x.levyra.ui.i18n.LocalLevyraStrings
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

/** Text-led archive navigation. No reused 2x2 Levyra library cards. */
@Composable
internal fun AuriaLibraryAtlas(
    mode: AuriaVisualMode,
    entries: List<LibrarySectionEntry>,
    deviceSongCount: Int,
    favorites: List<Track>,
    recentlyPlayed: List<Track>,
    onOpen: (LibraryCategory) -> Unit,
    onPlay: (Track, List<Track>) -> Unit,
    onPlayDevice: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val strings = LocalLevyraStrings.current
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (mode == AuriaVisualMode.Pulse) 9.dp else 12.dp)
    ) {
        Box(
            Modifier.fillMaxWidth().height(146.dp)
                .clip(RoundedCornerShape(topStart = 11.dp, topEnd = 46.dp, bottomStart = 46.dp, bottomEnd = 11.dp))
                .background(Brush.horizontalGradient(listOf(colors.primaryContainer, colors.surfaceContainerHigh))),
        ) {
            Column(Modifier.align(Alignment.CenterStart).padding(17.dp)) {
                Text("01 / MIS ARCHIVOS", color = colors.primary, fontSize = 10.sp,
                    fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
                Text("EN ESTE DISPOSITIVO", color = colors.onSurface,
                    fontSize = 17.sp, fontWeight = FontWeight.Black)
                Text("$deviceSongCount  ${strings.songs}", color = colors.onSurfaceVariant, fontSize = 12.sp)
                Row(
                    Modifier.padding(top = 10.dp).clip(RoundedCornerShape(9.dp))
                        .background(colors.primary).clickable(onClick = { onOpen(LibraryCategory.Device) })
                        .padding(horizontal = 13.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ABRIR", color = colors.onPrimary, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Rounded.ArrowForward, null, tint = colors.onPrimary,
                        modifier = Modifier.size(17.dp))
                }
            }
            Icon(Icons.Rounded.PhoneAndroid, null, tint = colors.primary.copy(alpha = .35f),
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 26.dp).size(66.dp))
        }
        Text("02 / DIRECTORIO", color = colors.primary, fontSize = 11.sp,
            letterSpacing = 1.4.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 7.dp))
        entries.filter { it.category != LibraryCategory.Device }.forEachIndexed { index, entry ->
            Row(
                Modifier.fillMaxWidth().height(57.dp)
                    .clip(RoundedCornerShape(if (mode == AuriaVisualMode.Studio) 5.dp else 12.dp))
                    .background(colors.surfaceContainer)
                    .clickable(onClick = { onOpen(entry.category) })
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text((index + 1).toString().padStart(2, '0'), color = colors.primary,
                    fontWeight = FontWeight.Black, fontSize = 12.sp)
                Text(entry.category.libraryLabel(strings), color = colors.onSurface, fontSize = 15.sp,
                    fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                if (entry.summary.isNotBlank()) {
                    Text(entry.summary, color = colors.onSurfaceVariant,
                        fontSize = 11.sp, maxLines = 1, modifier = Modifier.weight(.75f),
                        overflow = TextOverflow.Ellipsis)
                }
                Icon(Icons.Rounded.ArrowForward, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }
        if (favorites.isNotEmpty()) {
            Text("03 / EN ROTACIÓN", color = colors.primary, fontSize = 11.sp,
                letterSpacing = 1.4.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 10.dp))
            favorites.take(4).forEachIndexed { i, track ->
                AuriaLibraryTrackRow(i, track) { onPlay(track, favorites) }
            }
        }
        if (recentlyPlayed.isNotEmpty()) {
            Text("04 / TU HISTORIAL", color = colors.primary, fontSize = 11.sp,
                letterSpacing = 1.4.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 9.dp))
            recentlyPlayed.take(3).forEachIndexed { i, track ->
                AuriaLibraryTrackRow(i, track) { onPlay(track, recentlyPlayed) }
            }
        }
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(9.dp))
                .background(colors.surfaceContainerHigh)
                .clickable(onClick = onPlayDevice)
                .padding(vertical = 15.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.PlayArrow, null, tint = colors.primary)
            Spacer(Modifier.width(9.dp))
            Text("REPRODUCIR BIBLIOTECA LOCAL", color = colors.onSurface,
                fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
internal fun AuriaLibraryTop(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)?
) {
    val colors = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver", tint = colors.onSurface)
            }
        }
        Column(Modifier.weight(1f)) {
            Text("AURIA   /   VAULT", color = colors.primary, letterSpacing = 2.sp,
                fontSize = 10.sp, fontWeight = FontWeight.Black)
            Text(title, color = colors.onSurface, fontSize = 28.sp,
                lineHeight = 32.sp, fontWeight = FontWeight.Black)
            if (subtitle.isNotBlank()) Text(subtitle, color = colors.onSurfaceVariant, fontSize = 12.sp)
        }
        AuriaHaloEmblem(Modifier.size(38.dp), colors.primary, colors.tertiary)
    }
}

@Composable
private fun AuriaLibraryTrackRow(index: Int, track: Track, onPlay: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().height(65.dp).clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceContainer).clickable(onClick = onPlay)
            .padding(horizontal = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Text((index + 1).toString().padStart(2, '0'), color = colors.primary,
            fontSize = 12.sp, fontWeight = FontWeight.Black)
        AsyncImage(
            model = track.thumbnailUrl.ifBlank { track.largeThumbnailUrl }, contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(46.dp).clip(RoundedCornerShape(5.dp))
        )
        Column(Modifier.weight(1f)) {
            Text(track.title, color = colors.onSurface, fontWeight = FontWeight.Bold,
                fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, color = colors.onSurfaceVariant, fontSize = 11.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Icon(Icons.Rounded.PlayArrow, null, tint = colors.primary, modifier = Modifier.size(22.dp))
    }
}
