package com.luc4n3x.levyra.ui.player

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.luc4n3x.levyra.player.AuriaDjTransition

@Composable
internal fun AuriaDjArtworkTransition(
    transition: AuriaDjTransition?,
    animated: Boolean,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = transition,
        animationSpec = tween(if (animated) 300 else 0),
        label = "auria-dj-artwork-transition",
        modifier = modifier
    ) { mix ->
        if (mix != null) {
            val reveal = remember(mix.sourceId, mix.targetId) { Animatable(0f) }
            LaunchedEffect(mix.sourceId, mix.targetId, animated) {
                if (animated) reveal.animateTo(1f, tween(500)) else reveal.snapTo(1f)
            }
            val primary = MaterialTheme.colorScheme.primary
            val secondary = MaterialTheme.colorScheme.tertiary
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                primary.copy(alpha = 0.52f),
                                Color(0xFF141323),
                                secondary.copy(alpha = 0.42f),
                                Color(0xFF10131E)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "AURIA · DJ MIX",
                        color = Color.White.copy(alpha = 0.88f),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MixArtwork(
                            url = mix.sourceArtwork,
                            alpha = 1f - reveal.value * 0.10f,
                            translationFraction = 1f - reveal.value,
                            modifier = Modifier.weight(1f)
                        )
                        MixArtwork(
                            url = mix.targetArtwork,
                            alpha = reveal.value,
                            translationFraction = reveal.value - 1f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = mix.targetTitle,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = mix.targetArtist,
                        color = Color.White.copy(alpha = 0.74f),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun MixArtwork(
    url: String,
    alpha: Float,
    translationFraction: Float,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                this.alpha = alpha.coerceIn(0f, 1f)
                translationX = 18.dp.toPx() * translationFraction
            }
            .clip(shape)
            .background(Color.White.copy(alpha = 0.09f))
            .border(1.dp, Color.White.copy(alpha = 0.25f), shape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
