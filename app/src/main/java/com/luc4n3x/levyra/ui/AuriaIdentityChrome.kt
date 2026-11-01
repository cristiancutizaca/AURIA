package com.luc4n3x.levyra.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.luc4n3x.levyra.domain.LevyraTab
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

/**
 * Native AURIA navigation: anchored brand rail + four signal stations.
 * It does not use Levyra's FloatingDock/Minimal/Connected components.
 */
@Composable
internal fun AuriaPrimaryDock(
    mode: AuriaVisualMode,
    selected: LevyraTab,
    onSelect: (LevyraTab) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val angular = mode == AuriaVisualMode.Studio || mode == AuriaVisualMode.Pulse
    val shell = if (angular) RoundedCornerShape(13.dp) else RoundedCornerShape(30.dp)
    Row(
        Modifier.fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .clip(shell)
            .background(colors.surfaceContainerHigh)
            .navigationBarsPadding()
            .padding(horizontal = 9.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(Modifier.size(39.dp), contentAlignment = Alignment.Center) {
            AuriaHaloEmblem(Modifier.size(37.dp), colors.primary, colors.tertiary)
        }
        Box(Modifier.size(width = 1.dp, height = 32.dp)
            .background(colors.outlineVariant.copy(alpha = .6f)))
        val entries = listOf(
            Triple(LevyraTab.Home, Icons.Rounded.GraphicEq, "Inicio"),
            Triple(LevyraTab.Search, Icons.Rounded.Search, "Buscar"),
            Triple(LevyraTab.Explore, Icons.Rounded.Explore, "Explorar"),
            Triple(LevyraTab.Library, Icons.Rounded.LibraryMusic, "Mi música")
        )
        entries.forEach { (tab, icon, label) ->
            val active = selected == tab
            val buttonShape = if (angular) RoundedCornerShape(9.dp) else RoundedCornerShape(19.dp)
            Column(
                Modifier.weight(1f)
                    .height(60.dp)
                    .clip(buttonShape)
                    .background(if (active) colors.primaryContainer else Color.Transparent)
                    .clickable(role = Role.Tab, onClickLabel = label) { onSelect(tab) }
                    .semantics { this.selected = active }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(icon, contentDescription = label,
                    modifier = Modifier.size(23.dp),
                    tint = if (active) colors.onPrimaryContainer else colors.onSurfaceVariant)
                Spacer(Modifier.height(3.dp))
                if (active) {
                    Text(label, color = colors.onPrimaryContainer,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp)
                } else {
                    Box(Modifier.size(width = 10.dp, height = 2.dp)
                        .background(colors.onSurfaceVariant.copy(alpha = .35f), CircleShape))
                }
            }
        }
    }
}

/**
 * Dedicated AURIA mini-console. The playback engine and queue callbacks
 * remain untouched; only the presentation is replaced.
 */
@Composable
internal fun AuriaCompactPlayer(
    mode: AuriaVisualMode,
    model: MiniPlayerModel,
    actions: MiniPlayerPlaybackActions
) {
    val colors = MaterialTheme.colorScheme
    val angular = mode == AuriaVisualMode.Studio || mode == AuriaVisualMode.Pulse
    val shape = if (angular) RoundedCornerShape(12.dp) else RoundedCornerShape(
        topStart = 28.dp, topEnd = 13.dp, bottomStart = 13.dp, bottomEnd = 28.dp
    )
    val image = model.track.largeThumbnailUrl.ifBlank { model.track.thumbnailUrl }
    Column(
        Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(colors.surfaceContainer, colors.surfaceContainerHigh, colors.surfaceContainer)
                )
            )
    ) {
        Row(
            Modifier.fillMaxWidth().height(67.dp).padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Box(
                Modifier.size(47.dp).clip(RoundedCornerShape(if (angular) 7.dp else 15.dp))
                    .background(colors.secondaryContainer)
                    .clickable(role = Role.Button, onClick = actions.open),
                contentAlignment = Alignment.Center
            ) {
                if (image.isNotBlank()) {
                    AsyncImage(model = image, contentDescription = model.track.title,
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Icon(Icons.Rounded.GraphicEq, contentDescription = null,
                        tint = colors.primary)
                }
            }
            Column(Modifier.weight(1f).clickable(role = Role.Button, onClick = actions.open),
                verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    if (model.isPlaying) "●  AURIA EN VIVO" else "◉  EN TU REPRODUCTOR",
                    color = colors.primary, fontSize = 9.sp,
                    fontWeight = FontWeight.Black, letterSpacing = 1.sp,
                    maxLines = 1
                )
                Text(model.track.title, maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colors.onSurface,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold)
                Text(model.liveNowPlaying.ifBlank { model.track.artist },
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = actions.toggle, modifier = Modifier.size(43.dp)
                .clip(CircleShape).background(colors.primary)) {
                Icon(if (model.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (model.isPlaying) "Pausar" else "Reproducir",
                    tint = colors.onPrimary)
            }
            IconButton(onClick = actions.next, modifier = Modifier.size(41.dp)) {
                Icon(Icons.Rounded.SkipNext, contentDescription = "Siguiente",
                    tint = colors.onSurface)
            }
        }
        // Real playback fraction, not a decorative fake waveform.
        Canvas(Modifier.fillMaxWidth().height(3.dp)) {
            drawRect(colors.onSurface.copy(alpha = .12f))
            drawRect(colors.primary,
                size = androidx.compose.ui.geometry.Size(
                    width = size.width * model.progress.coerceIn(0f, 1f),
                    height = size.height
                )
            )
        }
    }
}
