package com.luc4n3x.levyra.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

/**
 * Static, lightweight AURIA canvas, not Levyra's shared atmospheric image.
 * Four genuinely different geometries; no animation when idle and no GPU blur.
 */
@Composable
internal fun AuriaIndependentBackdrop(mode: AuriaVisualMode) {
    val colors = MaterialTheme.colorScheme
    val ink = colors.background
    val accent = colors.primary
    val secondary = colors.tertiary
    Canvas(Modifier.fillMaxSize()) {
        drawRect(
            Brush.verticalGradient(listOf(ink, colors.surface.copy(alpha = 0.98f), ink))
        )
        when (mode) {
            AuriaVisualMode.Flow -> {
                for (i in 0..4) {
                    val bend = size.width * (.5f + i * .13f)
                    val y = size.height * (.12f + i * .14f)
                    val path = Path().apply {
                        moveTo(-size.width * .2f, y + size.width * .26f)
                        cubicTo(bend * .2f, y - 80f, bend * .85f, y + 125f, size.width * 1.15f, y - 40f)
                    }
                    drawPath(path, accent.copy(alpha = .045f + i * .012f), style = Stroke(width = 2f + i))
                }
            }
            AuriaVisualMode.Nebula -> {
                val center = Offset(size.width * .81f, size.height * .25f)
                (1..5).forEach { ring ->
                    drawCircle(
                        if (ring % 2 == 0) secondary.copy(alpha = .12f) else accent.copy(alpha = .10f),
                        radius = size.width * (.24f + ring * .14f),
                        center = center,
                        style = Stroke(width = if (ring == 1) 3f else 1.5f)
                    )
                }
            }
            AuriaVisualMode.Pulse -> {
                val barSpace = (size.width / 19f).coerceAtLeast(1f)
                repeat(19) { i ->
                    val height = size.height * (.05f + ((i * 7 + 4) % 13) / 130f)
                    val x = i * barSpace
                    drawLine(
                        if (i % 4 == 0) secondary.copy(alpha = .15f) else accent.copy(alpha = .09f),
                        Offset(x, size.height * .23f - height / 2),
                        Offset(x, size.height * .23f + height / 2),
                        strokeWidth = barSpace * .27f
                    )
                }
            }
            AuriaVisualMode.Studio -> {
                val step = (size.width / 9f).coerceAtLeast(1f)
                repeat(10) { i ->
                    val x = i * step
                    drawLine(accent.copy(alpha = .07f), Offset(x, 0f),
                        Offset(x, size.height), strokeWidth = 1f)
                }
                repeat(25) { i ->
                    val y = i * step
                    drawLine(secondary.copy(alpha = .055f), Offset(0f, y),
                        Offset(size.width, y), strokeWidth = 1f)
                }
            }
        }
    }
}
