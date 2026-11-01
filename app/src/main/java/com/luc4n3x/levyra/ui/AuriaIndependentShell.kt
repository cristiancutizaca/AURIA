package com.luc4n3x.levyra.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.luc4n3x.levyra.domain.LevyraTab
import com.luc4n3x.levyra.ui.player.PlayerMorphAnchors
import com.luc4n3x.levyra.ui.player.PlayerMorphSlot
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

/**
 * AURIA's own command-line shell, deliberately not an adaptation of Levyra's
 * floating bottom dock. One active destination dominates; the others act as
 * quick commands. No inherited nav presets, icons or card shapes.
 */
@Composable
internal fun AuriaCommandDock(
    mode: AuriaVisualMode,
    selected: LevyraTab,
    onSelect: (LevyraTab) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val entries = rememberLevyraTabEntries()
    val active = entries.firstOrNull { it.tab == selected } ?: entries.first()
    val others = entries.filter { it.tab != selected }
    val shape = when (mode) {
        AuriaVisualMode.Flow -> RoundedCornerShape(topStart = 8.dp, topEnd = 30.dp, bottomStart = 30.dp, bottomEnd = 8.dp)
        AuriaVisualMode.Nebula -> RoundedCornerShape(40.dp)
        AuriaVisualMode.Pulse -> RoundedCornerShape(10.dp)
        AuriaVisualMode.Studio -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .height(if (mode == AuriaVisualMode.Pulse) 60.dp else 68.dp)
            .clip(shape)
            .background(colors.surfaceContainerHigh)
            .border(BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.6f)), shape)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(16.dp))
                .clickable(role = Role.Tab, onClick = { onSelect(active.tab) }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(Modifier.size(37.dp).background(colors.primary, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(active.selectedIcon, active.label, tint = colors.onPrimary, modifier = Modifier.size(22.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text("AURIA  /  0${active.tab.ordinal + 1}", color = colors.primary,
                    fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                Text(active.label, color = colors.onSurface, fontSize = 15.sp,
                    fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Box(Modifier.width(1.dp).height(32.dp).background(colors.outlineVariant))
        others.forEach { entry ->
            IconButton(
                modifier = Modifier.size(42.dp),
                onClick = { onSelect(entry.tab) }
            ) {
                Icon(entry.unselectedIcon, entry.label, tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(22.dp))
            }
        }
    }
}

/** An independent two-deck transport strip, not a resized Levyra player. */
@Composable
internal fun AuriaTransportStrip(
    mode: AuriaVisualMode,
    model: MiniPlayerModel,
    morphAnchors: PlayerMorphAnchors?,
    artworkHidden: () -> Boolean,
    playbackActions: MiniPlayerPlaybackActions,
    expansionActions: MiniPlayerExpansionActions
) {
    val colors = MaterialTheme.colorScheme
    val track = model.track
    var verticalDistance by remember { mutableFloatStateOf(0f) }
    val shape = when (mode) {
        AuriaVisualMode.Nebula -> RoundedCornerShape(topStart = 36.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 36.dp)
        AuriaVisualMode.Flow -> RoundedCornerShape(topStart = 12.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 12.dp)
        AuriaVisualMode.Pulse -> RoundedCornerShape(12.dp)
        AuriaVisualMode.Studio -> RoundedCornerShape(5.dp)
    }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 4.dp)
            .onGloballyPositioned { morphAnchors?.update(PlayerMorphSlot.MiniCard, it.boundsInRoot()) }
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(colors.surfaceContainerHighest, colors.surfaceContainerHigh, colors.surface)
                )
            )
            .border(1.dp, colors.outlineVariant.copy(alpha = 0.7f), shape)
            .pointerInput(expansionActions) {
                detectVerticalDragGestures(
                    onDragStart = { verticalDistance = 0f; expansionActions.start() },
                    onVerticalDrag = { change, amount ->
                        change.consume()
                        verticalDistance += amount
                        expansionActions.drag(amount)
                    },
                    onDragEnd = { expansionActions.end(0f) },
                    onDragCancel = { expansionActions.end(0f) }
                )
            }
    ) {
        Row(
            Modifier.fillMaxWidth().height(if (mode == AuriaVisualMode.Pulse) 66.dp else 74.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Box(
                Modifier.size(49.dp).clip(RoundedCornerShape(9.dp))
                    .background(colors.primaryContainer)
                    .clickable(onClick = playbackActions.open)
                    .onGloballyPositioned { morphAnchors?.update(PlayerMorphSlot.Mini, it.boundsInRoot()) }
            ) {
                AsyncImage(
                    model = track.largeThumbnailUrl.ifBlank { track.thumbnailUrl },
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight().alpha(if (artworkHidden()) 0f else 1f)
                )
            }
            Column(
                Modifier.weight(1f).clickable(onClick = playbackActions.open),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text("EN FRECUENCIA", color = colors.primary, fontWeight = FontWeight.Bold,
                    fontSize = 9.sp, letterSpacing = 1.4.sp)
                Text(track.title, color = colors.onSurface, fontSize = 15.sp,
                    fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(track.artist, color = colors.onSurfaceVariant, fontSize = 11.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Box(
                Modifier.size(45.dp).clip(RoundedCornerShape(13.dp))
                    .background(colors.primary).clickable(
                        role = Role.Button,
                        onClick = playbackActions.toggle
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(if (model.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (model.isPlaying) "Pausar" else "Reproducir",
                    tint = colors.onPrimary, modifier = Modifier.size(25.dp))
            }
            IconButton(onClick = playbackActions.next, modifier = Modifier.size(37.dp)) {
                Icon(Icons.Rounded.SkipNext, contentDescription = "Siguiente", tint = colors.onSurface)
            }
        }
        Box(Modifier.fillMaxWidth().height(3.dp).background(colors.outlineVariant.copy(alpha = .45f))) {
            Box(
                Modifier.fillMaxWidth(model.progress.coerceIn(0f, 1f))
                    .fillMaxHeight().background(colors.primary)
            )
        }
    }
}
