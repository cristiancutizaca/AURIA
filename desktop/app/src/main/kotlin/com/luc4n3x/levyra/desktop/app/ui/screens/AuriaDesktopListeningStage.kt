package com.luc4n3x.levyra.desktop.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luc4n3x.levyra.desktop.app.ui.components.Artwork
import com.luc4n3x.levyra.desktop.app.ui.components.AuriaHaloMark
import com.luc4n3x.levyra.desktop.core.model.Track

/**
 * AURIA's own listening-first desktop masthead.
 * Playback actions and catalog navigation are delegated to the existing app.
 */
@Composable
internal fun AuriaDesktopListeningStage(
    greeting: String,
    subtitle: String,
    featured: Track?,
    playLabel: String,
    searchLabel: String,
    exploreLabel: String,
    onPlay: () -> Unit,
    onSearch: () -> Unit,
    onExplore: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(topStart = 12.dp, topEnd = 60.dp, bottomStart = 42.dp, bottomEnd = 12.dp)
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AuriaHaloMark(Modifier.size(48.dp), primary = colors.primary)
            Column(Modifier.weight(1f)) {
                Text("AURIA  /  01  ·  SOUND SYSTEM", color = colors.primary,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text(greeting, color = colors.onBackground,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black)
            }
        }
        BoxWithConstraints(
            Modifier.fillMaxWidth().clip(shape)
                .background(
                    Brush.horizontalGradient(
                        listOf(colors.surfaceContainerHigh, colors.secondaryContainer, colors.surface)
                    )
                )
        ) {
            val compact = maxWidth < 640.dp
            val artSize = if (compact) 140.dp else 210.dp
            Canvas(Modifier.fillMaxSize()) {
                val center = Offset(size.width * .84f, size.height * .54f)
                val d = size.height * .36f
                drawCircle(colors.primary.copy(alpha = .22f), d, center, style = Stroke(2.dp.toPx()))
                drawCircle(colors.tertiary.copy(alpha = .22f), d * 1.4f, center, style = Stroke(1.dp.toPx()))
            }
            Row(
                Modifier.fillMaxWidth().padding(if (compact) 20.dp else 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("AHORA EN TU UNIVERSO", color = colors.primary,
                        fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text(featured?.title ?: subtitle,
                        maxLines = 2, overflow = TextOverflow.Ellipsis,
                        style = if (compact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black, color = colors.onSurface)
                    Text(featured?.displaySubtitle.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onPlay) { Text(playLabel) }
                        OutlinedButton(onClick = onExplore) { Text(exploreLabel) }
                    }
                }
                Box(
                    Modifier.size(artSize)
                        .clip(RoundedCornerShape(if (compact) 20.dp else 35.dp))
                        .background(colors.surfaceContainerHighest)
                        .clickable { onPlay() },
                    contentAlignment = Alignment.Center
                ) {
                    if (featured != null) {
                        Artwork(url = featured.artworkUrl, modifier = Modifier.fillMaxSize(),
                            cornerRadius = if (compact) 20.dp else 35.dp,
                            iconSize = 52.dp)
                    } else {
                        AuriaHaloMark(Modifier.size(95.dp), colors.primary)
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceContainerLow)
                .clickable(onClick = onSearch)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("⌕", color = colors.primary, fontSize = 24.sp)
            Text(searchLabel, color = colors.onSurfaceVariant, modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge)
            Text("BUSCAR  ↗", color = colors.primary, fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp, fontSize = 11.sp)
        }
    }
}
