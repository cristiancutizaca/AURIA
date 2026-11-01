package com.luc4n3x.levyra.ui.recognition

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.luc4n3x.levyra.feature.recognition.RecognitionState
import com.luc4n3x.levyra.ui.AuriaHaloEmblem
import com.luc4n3x.levyra.ui.AuriaSignalRibbon
import com.luc4n3x.levyra.ui.i18n.LevyraStrings

/**
 * Distinctive AURIA Halo recognition entry point. Never records on its own;
 * the explicit microphone/device buttons below own the permission flow.
 */
@Composable
internal fun AuriaRecognitionHalo(state: RecognitionState, strings: LevyraStrings) {
    val colors = MaterialTheme.colorScheme
    val busy = state == RecognitionState.Listening || state == RecognitionState.Identifying
    val pulse = if (busy) {
        val transition = rememberInfiniteTransition(label = "auria-recognition-listening")
        val animated = transition.animateFloat(
            initialValue = 0.84f,
            targetValue = 1.10f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1450),
                repeatMode = RepeatMode.Reverse
            ),
            label = "auria-recognition-pulse"
        )
        animated.value
    } else {
        1f
    }
    val shape = RoundedCornerShape(
        topStart = 36.dp,
        topEnd = 14.dp,
        bottomEnd = 36.dp,
        bottomStart = 14.dp
    )
    val status = when (state) {
        RecognitionState.Idle -> strings.recognitionTapToListen
        RecognitionState.Listening -> strings.recognitionListening
        RecognitionState.Identifying -> strings.recognitionProcessing
        is RecognitionState.Result -> state.result.title
        RecognitionState.NoMatch -> strings.recognitionNoMatch
        is RecognitionState.Error -> strings.recognitionFailed
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(colors.surfaceContainerHigh, colors.surface, colors.surfaceContainer)
                )
            )
            .border(BorderStroke(1.dp, colors.primary.copy(alpha = 0.35f)), shape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height * .40f)
            val r = size.minDimension * .37f * pulse
            drawCircle(
                color = colors.primary.copy(alpha = if (busy) .22f else .12f),
                radius = r,
                center = c,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = colors.tertiary.copy(alpha = .20f),
                radius = r * 1.24f,
                center = c,
                style = Stroke(width = 1.dp.toPx())
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AuriaHaloEmblem(
                modifier = Modifier.size(93.dp),
                primary = colors.primary,
                secondary = colors.tertiary
            )
            Text(
                text = "AURIA / " + strings.recognizeMusic,
                color = colors.primary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = status,
                color = colors.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            AuriaSignalRibbon(
                modifier = Modifier.fillMaxWidth(.62f),
                primary = colors.primary,
                secondary = colors.tertiary
            )
        }
    }
}
