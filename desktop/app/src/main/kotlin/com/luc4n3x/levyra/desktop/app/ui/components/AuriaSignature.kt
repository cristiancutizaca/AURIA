package com.luc4n3x.levyra.desktop.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** AURIA: original broken-halo symbol, with a twin-note center. */
@Composable
fun AuriaHaloMark(
    modifier: Modifier = Modifier,
    primary: Color = Color(0xFFB09AFF),
    secondary: Color = Color(0xFFFF9E8A)
) {
    Canvas(modifier) {
        val d = size.minDimension
        val c = center
        drawCircle(color = primary.copy(alpha = 0.13f), radius = d * 0.49f)
        val ellipse = Size(d * 0.72f, d * 0.72f)
        val topLeft = Offset(c.x - d * 0.36f, c.y - d * 0.36f)
        drawArc(primary, 155f, 276f, false, topLeft, ellipse,
            style = Stroke(width = d * 0.055f, cap = StrokeCap.Round))
        drawArc(secondary, 80f, 68f, false, topLeft, ellipse,
            style = Stroke(width = d * 0.055f, cap = StrokeCap.Round))
        val ink = Color(0xFFF8F4FF)
        val stemX = c.x + d * 0.08f
        val top = c.y - d * 0.17f
        val bottom = c.y + d * 0.19f
        drawLine(ink, Offset(stemX, top), Offset(stemX, bottom), d * 0.065f,
            cap = StrokeCap.Round)
        drawLine(ink, Offset(stemX - d * 0.20f, top + d * 0.07f), Offset(stemX, top),
            d * 0.06f, cap = StrokeCap.Round)
        drawCircle(ink, d * 0.065f, Offset(stemX - d * 0.06f, bottom))
        drawCircle(ink, d * 0.062f, Offset(stemX - d * 0.24f, bottom - d * 0.10f))
        drawLine(ink, Offset(stemX - d * 0.18f, top + d * 0.06f),
            Offset(stemX - d * 0.18f, bottom - d * 0.10f), d * 0.055f,
            cap = StrokeCap.Round)
    }
}

/** AURIA ambient atmosphere, behind all of the desktop navigation. */
@Composable
fun AuriaDesktopAtmosphere(modifier: Modifier = Modifier, accent: Color) {
    Canvas(modifier) {
        val anchor = Offset(size.width * 0.89f, size.height * 0.13f)
        val radius = size.minDimension * 0.61f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(accent.copy(alpha = 0.17f), accent.copy(alpha = 0.04f), Color.Transparent),
                center = anchor,
                radius = radius
            ),
            radius = radius,
            center = anchor
        )
        repeat(3) { ring ->
            drawCircle(
                color = accent.copy(alpha = 0.045f + ring * 0.012f),
                radius = size.minDimension * (0.23f + ring * 0.1f),
                center = anchor,
                style = Stroke(width = 1.dp.toPx())
            )
        }
        repeat(17) { point ->
            drawCircle(
                color = accent.copy(alpha = 0.09f),
                radius = if (point % 4 == 0) 2.dp.toPx() else 1.dp.toPx(),
                center = Offset(size.width * (0.42f + (point % 6) * 0.105f),
                    size.height * (0.09f + (point / 6) * 0.16f))
            )
        }
    }
}

/** Shared AURIA section identity; display copy remains localized by the caller. */
@Composable
fun AuriaDesktopSectionLabel(title: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(modifier, verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        AuriaHaloMark(Modifier.size(28.dp), colors.primary, colors.tertiary)
        Text("AURIA / " + title, color = colors.primary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold)
    }
}

/** DJ ribbon belongs to AURIA, not to the inherited source components. */
@Composable
fun AuriaDesktopDjSignal(
    modifier: Modifier = Modifier,
    accent: Color,
    secondary: Color
) {
    Canvas(modifier.height(25.dp)) {
        repeat(33) { i ->
            val level = (5 + ((i * 17 + 3) % 18)) / 23f
            val h = size.height * level
            val x = size.width * (i + 0.5f) / 33f
            drawLine(
                color = if (i % 8 == 0) secondary else accent,
                start = Offset(x, (size.height - h) / 2f),
                end = Offset(x, (size.height + h) / 2f),
                strokeWidth = size.width / 68f,
                cap = StrokeCap.Round
            )
        }
    }
}
