package com.luc4n3x.levyra.ui.theme

import androidx.compose.foundation.BorderStroke
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
                    val shape = RoundedCornerShape(if (mode == AuriaVisualMode.Pulse) 14.dp else 24.dp)
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
                            Box(Modifier.fillMaxWidth().height(if (mode == AuriaVisualMode.Pulse) 6.dp else 10.dp)
                                .background(Brush.horizontalGradient(listOf(palette.blue, palette.pink)), CircleShape))
                        }
                    }
                }
            }
        }
    }
}
