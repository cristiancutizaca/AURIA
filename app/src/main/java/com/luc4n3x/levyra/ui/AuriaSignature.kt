package com.luc4n3x.levyra.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** AURIA's own halo / music-note glyph, shared by the six primary screens. */
@Composable internal fun AuriaHaloEmblem(modifier: Modifier = Modifier, primary: Color, secondary: Color) {
    Canvas(modifier) {
        val d = size.minDimension
        val c = center
        val box = Size(d * .76f, d * .76f)
        val p = Offset(c.x - box.width / 2f, c.y - box.height / 2f)
        drawCircle(primary.copy(alpha = .12f), d * .48f)
        drawArc(primary, -74f, 283f, false, p, box,
            style = Stroke(width = d * .055f, cap = StrokeCap.Round))
        drawArc(secondary, 210f, 67f, false, p, box,
            style = Stroke(width = d * .055f, cap = StrokeCap.Round))
        val note = Color(0xFFF4EEFF)
        drawLine(note, Offset(c.x + d * .12f, c.y - d * .16f),
            Offset(c.x + d * .12f, c.y + d * .18f), d * .065f, cap = StrokeCap.Round)
        drawLine(note, Offset(c.x - d * .13f, c.y - d * .07f),
            Offset(c.x + d * .12f, c.y - d * .16f), d * .06f, cap = StrokeCap.Round)
        drawLine(note, Offset(c.x - d * .13f, c.y - d * .07f),
            Offset(c.x - d * .13f, c.y + d * .10f), d * .055f, cap = StrokeCap.Round)
        drawCircle(note, d * .068f, Offset(c.x + d * .045f, c.y + d * .18f))
        drawCircle(note, d * .06f, Offset(c.x - d * .20f, c.y + d * .10f))
    }
}

@Composable internal fun AuriaSectionBrand(label: String, modifier: Modifier = Modifier) {
    val palette = MaterialTheme.colorScheme
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        AuriaHaloEmblem(Modifier.size(23.dp), palette.primary, palette.tertiary)
        Text(label, color = palette.primary, fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium)
    }
}

@Composable internal fun AuriaSignalRibbon(modifier: Modifier = Modifier, primary: Color, secondary: Color) {
    Canvas(modifier.height(24.dp)) {
        repeat(27) { i ->
            val height = size.height * (5 + (i * 13 + 7) % 16) / 21f
            val x = size.width * (i + .5f) / 27f
            drawLine(if (i % 6 == 0) secondary else primary,
                Offset(x, (size.height - height) / 2f),
                Offset(x, (size.height + height) / 2f),
                size.width / 54f, cap = StrokeCap.Round)
        }
    }
}
