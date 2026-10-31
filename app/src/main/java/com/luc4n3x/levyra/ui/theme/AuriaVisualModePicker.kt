package com.luc4n3x.levyra.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.luc4n3x.levyra.ui.i18n.AuriaVisualCopy

@Composable
internal fun AuriaVisualModePicker(
    selectedId: String,
    onSelect: (String) -> Unit,
    languageCode: String
) {
    val copy = AuriaVisualCopy.from(languageCode)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(copy.heading, style = MaterialTheme.typography.titleLarge, color = LevyraText)
        Text(copy.description, style = MaterialTheme.typography.bodySmall, color = LevyraMuted)
        AuriaVisualMode.entries.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                pair.forEach { mode ->
                    val palette = LevyraThemes.byId(mode.themeId)
                    val isSelected = selectedId == mode.themeId
                    val shape = when (mode) {
                        AuriaVisualMode.Flow -> RoundedCornerShape(topStart = 28.dp, topEnd = 8.dp, bottomEnd = 28.dp, bottomStart = 8.dp)
                        AuriaVisualMode.Nebula -> RoundedCornerShape(30.dp)
                        AuriaVisualMode.Pulse -> RoundedCornerShape(10.dp)
                        AuriaVisualMode.Studio -> RoundedCornerShape(16.dp)
                    }
                    Surface(
                        modifier = Modifier.weight(1f)
                            .semantics { selected = isSelected }
                            .clickable(role = Role.RadioButton) { onSelect(mode.themeId) },
                        shape = shape,
                        color = palette.panel,
                        border = BorderStroke(if (isSelected) 2.dp else 1.dp,
                            if (isSelected) palette.cyan else palette.outline)
                    ) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(32.dp)
                                        .background(Brush.linearGradient(listOf(palette.blue, palette.violet)), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(palette.emoji, color = palette.black, style = MaterialTheme.typography.titleMedium)
                                }
                                Spacer(Modifier.weight(1f))
                                if (isSelected) Text("✓", color = palette.cyan)
                            }
                            Text(palette.label, color = palette.text, fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium)
                            Text(copy.descriptionOf(mode), color = palette.muted,
                                style = MaterialTheme.typography.bodySmall, minLines = 2)
                            AuriaModeMiniature(mode, palette)
                        }
                    }
                }
            }
        }
    }
}

/** Structural preview for each unique AURIA mode. */
@Composable
private fun AuriaModeMiniature(mode: AuriaVisualMode, palette: LevyraPalette) {
    Canvas(Modifier.fillMaxWidth().height(44.dp)) {
        val w = size.width
        val h = size.height
        when (mode) {
            AuriaVisualMode.Flow -> {
                drawRoundRect(
                    brush = Brush.horizontalGradient(listOf(palette.blue, palette.violet, palette.pink)),
                    topLeft = Offset(0f, h * 0.20f),
                    size = Size(w, h * 0.60f),
                    cornerRadius = CornerRadius(h * 0.30f)
                )
                drawCircle(palette.text.copy(alpha = 0.85f), radius = h * 0.12f,
                    center = Offset(w * 0.77f, h * 0.50f))
            }
            AuriaVisualMode.Nebula -> {
                drawCircle(palette.violet.copy(alpha = 0.17f), radius = h * 0.45f,
                    center = Offset(w * 0.50f, h * 0.50f))
                drawCircle(palette.cyan, radius = h * 0.36f,
                    center = Offset(w * 0.50f, h * 0.50f),
                    style = Stroke(width = h * 0.065f))
                drawCircle(palette.pink, radius = h * 0.09f,
                    center = Offset(w * 0.79f, h * 0.27f))
            }
            AuriaVisualMode.Pulse -> {
                repeat(11) { i ->
                    val barHeight = h * (0.26f + ((i * 7) % 9) * 0.07f)
                    val x = w * (i.toFloat() / 11f) + w * 0.018f
                    drawRoundRect(
                        color = if (i % 4 == 0) palette.pink else palette.cyan,
                        topLeft = Offset(x, (h - barHeight) / 2f),
                        size = Size(w * 0.055f, barHeight),
                        cornerRadius = CornerRadius(w * 0.02f)
                    )
                }
            }
            AuriaVisualMode.Studio -> {
                repeat(3) { i ->
                    val y = h * (0.19f + i * 0.30f)
                    drawLine(palette.muted.copy(alpha = 0.75f), Offset(w * 0.05f, y),
                        Offset(w * 0.95f, y), strokeWidth = h * 0.045f)
                    val knobX = w * (0.30f + i * 0.19f)
                    drawRoundRect(palette.cyan, Offset(knobX, y - h * 0.11f),
                        Size(w * 0.08f, h * 0.22f), CornerRadius(h * 0.05f))
                }
            }
        }
    }
}
