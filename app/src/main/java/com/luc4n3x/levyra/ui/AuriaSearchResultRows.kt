package com.luc4n3x.levyra.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.luc4n3x.levyra.domain.Track
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

/**
 * AURIA search catalog records are flat "signal entries", not nested
 * Levyra grouped tiles. Actions remain direct and selection stays intact.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AuriaSearchResultRow(
    track: Track,
    mode: AuriaVisualMode,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isResolving: Boolean,
    isFavorite: Boolean,
    selected: Boolean,
    selectionActive: Boolean,
    isDownloading: Boolean,
    isDownloaded: Boolean,
    downloadProgress: Int?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onDownload: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val active = isCurrent || selected
    val shape = when (mode) {
        AuriaVisualMode.Flow -> RoundedCornerShape(topStart = 5.dp, topEnd = 19.dp, bottomStart = 19.dp, bottomEnd = 5.dp)
        AuriaVisualMode.Nebula -> RoundedCornerShape(22.dp)
        AuriaVisualMode.Pulse -> RoundedCornerShape(7.dp)
        AuriaVisualMode.Studio -> RoundedCornerShape(2.dp)
    }
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(shape)
            .background(if (active) colors.primaryContainer else colors.surfaceContainer)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(modifier = Modifier.size(if (mode == AuriaVisualMode.Pulse) 43.dp else 54.dp)
            .clip(RoundedCornerShape(if (mode == AuriaVisualMode.Studio) 3.dp else 8.dp))
            .background(colors.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = track.thumbnailUrl.ifBlank { track.largeThumbnailUrl },
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                contentScale = ContentScale.Crop
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (isCurrent) {
                Text("●  ${if (isPlaying) "SONANDO" else if (isResolving) "CARGANDO" else "EN COLA"}",
                    color = colors.primary, fontSize = 9.sp, fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp)
            }
            Text(track.title, color = colors.onSurface, fontWeight = FontWeight.Bold,
                fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, color = colors.onSurfaceVariant,
                fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (isDownloading && downloadProgress != null) {
                Text("${downloadProgress.coerceIn(0, 100)}%", color = colors.tertiary,
                    fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
        if (selectionActive) {
            Icon(if (selected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = if (selected) "Seleccionada" else "Seleccionar",
                tint = colors.primary, modifier = Modifier.size(23.dp))
        } else {
            IconButton(onClick = onFavorite, modifier = Modifier.size(if (mode == AuriaVisualMode.Pulse) 33.dp else 40.dp)) {
                Icon(if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = "Favorito", tint = colors.primary,
                    modifier = Modifier.size(19.dp))
            }
            IconButton(onClick = onDownload, modifier = Modifier.size(39.dp)) {
                Icon(
                    if (isDownloaded) Icons.Rounded.DownloadDone else Icons.Rounded.Download,
                    contentDescription = if (isDownloaded) "Descargada" else if (isDownloading) "Descargando" else "Descargar",
                    tint = if (isDownloading) colors.tertiary else colors.onSurfaceVariant,
                    modifier = Modifier.size(21.dp)
                )
            }
            IconButton(onClick = onAddToPlaylist, modifier = Modifier.size(if (mode == AuriaVisualMode.Pulse) 32.dp else 37.dp)) {
                Icon(Icons.Rounded.Add, contentDescription = "Añadir a lista",
                    tint = colors.onSurface, modifier = Modifier.size(20.dp))
            }
        }
    }
}
