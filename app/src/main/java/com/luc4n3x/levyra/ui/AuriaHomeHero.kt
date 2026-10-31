package com.luc4n3x.levyra.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.luc4n3x.levyra.ui.i18n.AuriaVisualCopy
import com.luc4n3x.levyra.ui.i18n.LocalLevyraStrings
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode
import com.luc4n3x.levyra.ui.theme.LevyraActivePalette

@Composable
internal fun AuriaHomeHero(
    mode: AuriaVisualMode,
    title: String,
    artist: String,
    artwork: String,
    isPlaying: Boolean,
    onOpen: () -> Unit,
    header: (@Composable () -> Unit)?
) {
    val strings = LocalLevyraStrings.current
    val copy = AuriaVisualCopy.from(strings.code)
    val palette = LevyraActivePalette
    Column(Modifier.fillMaxWidth()) {
        header?.invoke()
        val backdrop = Brush.linearGradient(
            listOf(palette.panel, palette.ink, palette.panelSoft)
        )
        val shape = when (mode) {
            AuriaVisualMode.Flow -> RoundedCornerShape(topStart = 30.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 30.dp)
            AuriaVisualMode.Nebula -> RoundedCornerShape(36.dp)
            AuriaVisualMode.Pulse -> RoundedCornerShape(18.dp)
            AuriaVisualMode.Studio -> RoundedCornerShape(14.dp)
        }
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp)
                .clickable(role = Role.Button, onClickLabel = strings.playNow, onClick = onOpen),
            color = Color.Transparent,
            shape = shape,
            border = BorderStroke(1.dp, palette.outline)
        ) {
            BoxWithConstraints(Modifier.background(backdrop)) {
                val compact = maxWidth < 325.dp
                when (mode) {
                    AuriaVisualMode.Flow -> Row(
                        Modifier.fillMaxWidth().height(if (compact) 188.dp else 210.dp).padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(copy.radio, color = palette.cyan, style = MaterialTheme.typography.labelMedium)
                            Text(title.ifBlank { copy.hero }, color = palette.text, style = MaterialTheme.typography.headlineMedium,
                                maxLines = 3, overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.semantics { heading() })
                            Text(artist, color = palette.muted, style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            AuriaHeroPlay(palette.cyan, palette.black, isPlaying)
                        }
                        AuriaHeroArt(artwork, Modifier.size(if (compact) 94.dp else 134.dp), 26.dp)
                    }
                    AuriaVisualMode.Nebula -> Box(
                        Modifier.fillMaxWidth().height(240.dp)
                            .drawBehind {
                                val center = Offset(size.width * 0.73f, size.height * 0.42f)
                                val radius = size.width * 0.28f
                                drawCircle(palette.violet.copy(alpha = 0.32f), radius, center,
                                    style = Stroke(width = 2.dp.toPx()))
                                drawCircle(palette.pink.copy(alpha = 0.19f), radius * 1.22f, center,
                                    style = Stroke(width = 1.dp.toPx()))
                            }
                    ) {
                        AuriaHeroArt(artwork, Modifier.align(Alignment.CenterEnd)
                            .padding(end = 26.dp).size(132.dp), 66.dp)
                        Column(Modifier.align(Alignment.BottomStart).padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(copy.radio, color = palette.cyan, style = MaterialTheme.typography.labelMedium)
                            Text(title.ifBlank { copy.hero }, color = palette.text, style = MaterialTheme.typography.headlineMedium,
                                maxLines = 2, modifier = Modifier.semantics { heading() })
                            Text(artist, color = palette.muted, style = MaterialTheme.typography.bodySmall,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            AuriaHeroPlay(palette.cyan, palette.black, isPlaying)
                        }
                    }
                    AuriaVisualMode.Pulse -> Row(
                        Modifier.fillMaxWidth().height(126.dp).padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AuriaHeroArt(artwork, Modifier.size(94.dp), 12.dp)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Text(copy.radio, color = palette.cyan, style = MaterialTheme.typography.labelSmall)
                            Text(title.ifBlank { copy.hero }, color = palette.text, style = MaterialTheme.typography.titleLarge,
                                maxLines = 2, overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.semantics { heading() })
                            Text(artist, color = palette.muted, style = MaterialTheme.typography.bodySmall,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        AuriaHeroPlay(palette.cyan, palette.black, isPlaying)
                    }
                    AuriaVisualMode.Studio -> Column(
                        Modifier.fillMaxWidth().padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            AuriaHeroArt(artwork, Modifier.size(76.dp), 12.dp)
                            Column(Modifier.weight(1f)) {
                                Text(copy.radio, color = palette.cyan, style = MaterialTheme.typography.labelMedium)
                                Text(title.ifBlank { copy.hero }, color = palette.text, style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.semantics { heading() })
                                Text(artist, color = palette.muted, style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            AuriaHeroPlay(palette.cyan, palette.black, isPlaying)
                        }
                        AuriaStudioWaveform(palette.cyan, palette.violet)
                    }
                }
            }
        }
    }
}

@Composable
private fun AuriaHeroArt(url: String, modifier: Modifier, corner: androidx.compose.ui.unit.Dp) {
    Box(modifier.clip(RoundedCornerShape(corner))
        .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center) {
        if (url.isNotBlank()) {
            AsyncImage(model = url, contentDescription = null,
                modifier = Modifier.matchParentSize(), contentScale = ContentScale.Crop)
        } else {
            Icon(Icons.Rounded.GraphicEq, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun AuriaHeroPlay(accent: Color, ink: Color, playing: Boolean) {
    Box(Modifier.size(48.dp).background(accent, CircleShape), contentAlignment = Alignment.Center) {
        Icon(if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = null, tint = ink, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun AuriaStudioWaveform(accent: Color, secondary: Color) {
    Row(Modifier.fillMaxWidth().height(22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        val bars = listOf(9, 16, 12, 20, 7, 14, 18, 11, 21, 13, 8, 17, 10, 20, 14, 7, 16, 11)
        bars.forEachIndexed { index, height ->
            Box(Modifier.weight(1f).height(height.dp)
                .background(if (index % 3 == 0) secondary else accent,
                    RoundedCornerShape(3.dp)))
        }
    }
}
