package com.luc4n3x.levyra.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luc4n3x.levyra.domain.Track
import com.luc4n3x.levyra.ui.AuriaHaloEmblem
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

/**
 * AURIA V4's own expanded-player composition.
 * Slot callbacks retain playback, gestures, lyrics, queues, downloads
 * and transport controls. No state or media-engine migration is required.
 */
@Composable
internal fun AuriaIndependentPlayerDeck(
    track: Track,
    mode: AuriaVisualMode,
    slots: PlayerDeckSlots,
    compact: Boolean,
    gutter: Dp,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val corner = when (mode) {
        AuriaVisualMode.Flow -> RoundedCornerShape(topStart = 12.dp, topEnd = 78.dp, bottomStart = 78.dp, bottomEnd = 12.dp)
        AuriaVisualMode.Nebula -> RoundedCornerShape(72.dp)
        AuriaVisualMode.Pulse -> RoundedCornerShape(13.dp)
        AuriaVisualMode.Studio -> RoundedCornerShape(topStart = 5.dp, topEnd = 38.dp, bottomStart = 5.dp, bottomEnd = 38.dp)
    }
    val stageHeight = if (compact || mode == AuriaVisualMode.Pulse) 262.dp else 330.dp
    Column(
        modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = gutter, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 15.dp)
    ) {
        slots.header()
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("AURIA", color = colors.primary, fontWeight = FontWeight.Black,
                    fontSize = 11.sp, letterSpacing = 2.sp)
                Text(
                    when(mode) {
                        AuriaVisualMode.Flow -> "01 / FLOW"
                        AuriaVisualMode.Nebula -> "02 / NEBULA"
                        AuriaVisualMode.Pulse -> "03 / PULSE"
                        AuriaVisualMode.Studio -> "04 / STUDIO"
                    },
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
                AuriaHaloEmblem(Modifier.size(33.dp), colors.primary, colors.tertiary)
            }
            Box(
                Modifier.weight(1f).height(stageHeight)
                    .clip(corner)
                    .background(colors.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.matchParentSize()) {
                    if (mode == AuriaVisualMode.Nebula) {
                        repeat(3) { i ->
                            drawCircle(colors.primary.copy(alpha = .20f),
                                radius = size.minDimension * (.25f + i * .19f),
                                center = Offset(size.width * .5f, size.height * .5f),
                                style = Stroke(1.8.dp.toPx()))
                        }
                    }
                    if (mode == AuriaVisualMode.Studio) {
                        repeat(9) { i ->
                            val x = size.width * i / 9f
                            drawLine(colors.tertiary.copy(alpha = .13f),
                                Offset(x, 0f), Offset(x, size.height), 1.dp.toPx())
                        }
                    }
                }
                slots.stage(track, Modifier.fillMaxSize().padding(10.dp), 16.dp)
                slots.gestures(track, Modifier.matchParentSize())
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(width = 5.dp, height = 32.dp)
                .background(colors.primary, RoundedCornerShape(3.dp)))
            Column(Modifier.padding(start = 12.dp)) {
                Text("SEÑAL ACTIVA", color = colors.primary, fontSize = 10.sp,
                    fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                Text("AURIA / TU REPRODUCTOR", color = colors.onSurfaceVariant,
                    fontSize = 12.sp)
            }
        }
        // Existing metadata, seeking, playback, lyrics and DJ controls.
        slots.controls(track)
        Spacer(Modifier.height(12.dp))
    }
}
