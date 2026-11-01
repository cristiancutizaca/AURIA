package com.luc4n3x.levyra.ui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PlayArrow
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
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

/** Radar navigation replaces Levyra's four flower-icon category tiles. */
@Composable
internal fun AuriaExploreLanding(
    mode: AuriaVisualMode,
    onNew: () -> Unit,
    onSamples: () -> Unit,
    onMoods: () -> Unit,
    onRadio: () -> Unit,
    onJam: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 19.dp, vertical = 17.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text("AURIA  /  DISCOVERY", color = colors.primary,
            fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Black)
        Text("RADAR SONORO", color = colors.onSurface,
            fontSize = if (mode == AuriaVisualMode.Pulse) 30.sp else 37.sp,
            lineHeight = 39.sp, fontWeight = FontWeight.Black)
        Text("Explora sonidos, escenas y nuevos artistas",
            color = colors.onSurfaceVariant, fontSize = 13.sp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AuriaRadarAction("01", "Novedades", Modifier.weight(1f), onNew)
            AuriaRadarAction("02", "Samples", Modifier.weight(1f), onSamples)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AuriaRadarAction("03", "Ambientes", Modifier.weight(1f), onMoods)
            AuriaRadarAction("04", "Radio", Modifier.weight(1f), onRadio)
            AuriaRadarAction("05", "Jam", Modifier.weight(1f), onJam)
        }
    }
}

@Composable
private fun AuriaRadarAction(number: String, name: String, modifier: Modifier, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier.height(90.dp)
            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 22.dp, bottomStart = 22.dp, bottomEnd = 6.dp))
            .background(colors.surfaceContainer)
            .border(1.dp, colors.outlineVariant.copy(alpha = .5f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick).padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(number, color = colors.tertiary, fontWeight = FontWeight.Black, fontSize = 11.sp)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(name, color = colors.onSurface, fontWeight = FontWeight.Bold,
                fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ArrowForward, null, tint = colors.primary, modifier = Modifier.size(15.dp))
        }
    }
}

/**
 * AURIA's editorial release ledger replaces the full-height Levyra cover
 * carousel. No content is fabricated; cards use real Explore feed tracks.
 */
@Composable
internal fun AuriaExploreReleaseLedger(
    tracks: List<Track>,
    mode: AuriaVisualMode,
    onPlay: (Track) -> Unit,
    onAlbum: (Track) -> Unit,
    trackActions: @Composable (Track) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    if (tracks.isEmpty()) return
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 19.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("01    /    EN EL RADAR", color = colors.primary, fontSize = 11.sp,
            fontWeight = FontWeight.Black, letterSpacing = 1.7.sp)
        Row(
            Modifier.fillMaxWidth().height(if (mode == AuriaVisualMode.Pulse) 166.dp else 208.dp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 46.dp, bottomStart = 46.dp, bottomEnd = 10.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(colors.primaryContainer, colors.surfaceContainerHighest, colors.surface)
                    )
                )
                .clickable(onClick = { onPlay(tracks.first()) }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f).padding(17.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("ESTRENO DESTACADO", color = colors.primary, fontWeight = FontWeight.Bold,
                    fontSize = 10.sp, letterSpacing = 1.sp)
                Text(tracks.first().title, color = colors.onSurface, fontSize = 23.sp,
                    lineHeight = 26.sp, maxLines = 3, overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Black)
                Text(tracks.first().artist, color = colors.onSurfaceVariant,
                    fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = colors.primary)
                    Text("ESCUCHAR", color = colors.primary, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
            AsyncImage(
                model = tracks.first().largeThumbnailUrl.ifBlank { tracks.first().thumbnailUrl },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth(.42f).fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 52.dp, bottomStart = 52.dp))
            )
        }
        tracks.drop(1).take(6).forEachIndexed { i, track ->
            Row(
                Modifier.fillMaxWidth().height(70.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surfaceContainer).padding(horizontal = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Text((i + 2).toString().padStart(2, '0'), color = colors.primary,
                    fontWeight = FontWeight.Black, fontSize = 12.sp)
                AsyncImage(
                    model = track.thumbnailUrl.ifBlank { track.largeThumbnailUrl },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(6.dp)).clickable(onClick = { onAlbum(track) })
                )
                Column(
                    Modifier.weight(1f).clickable(onClick = { onPlay(track) }),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(track.title, color = colors.onSurface, maxLines = 1,
                        fontSize = 13.sp, fontWeight = FontWeight.Bold, overflow = TextOverflow.Ellipsis)
                    Text(track.artist, color = colors.onSurfaceVariant, maxLines = 1,
                        fontSize = 11.sp, overflow = TextOverflow.Ellipsis)
                }
                trackActions(track)
            }
        }
    }
}
