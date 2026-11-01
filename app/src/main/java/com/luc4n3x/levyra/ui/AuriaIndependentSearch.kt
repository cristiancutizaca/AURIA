package com.luc4n3x.levyra.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.luc4n3x.levyra.domain.Track
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

/**
 * Search acts like AURIA's command terminal, not a slightly recolored Levyra
 * discovery carousel. Full existing results, filters and actions stay intact.
 */
@Composable
internal fun AuriaSearchCommandPanel(
    mode: AuriaVisualMode,
    query: String,
    placeholder: String,
    recognitionAvailable: Boolean,
    recognitionBusy: Boolean,
    onBack: () -> Unit,
    onQuery: (String) -> Unit,
    onSubmit: (String) -> Unit,
    onRecognize: () -> Unit,
    onCancelRecognition: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val compact = mode == AuriaVisualMode.Pulse || mode == AuriaVisualMode.Studio
    Column(
        Modifier.fillMaxWidth().padding(bottom = 6.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 17.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.size(46.dp)) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver", tint = colors.onSurface)
            }
            Column(Modifier.weight(1f)) {
                Text("AURIA / FIND", fontSize = 10.sp, color = colors.primary,
                    fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text(if (compact) "Buscar" else "ENCUENTRA TU SONIDO", color = colors.onSurface,
                    fontSize = if (compact) 24.sp else 27.sp, lineHeight = 30.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Black)
            }
            AuriaHaloEmblem(Modifier.size(38.dp), colors.primary, colors.tertiary)
        }
        Row(
            Modifier.fillMaxWidth()
                .height(62.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 20.dp))
                .background(colors.surfaceContainerHigh)
                .border(1.dp, colors.outlineVariant.copy(alpha = .75f), RoundedCornerShape(12.dp))
                .padding(start = 15.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = colors.primary, modifier = Modifier.size(23.dp))
            Spacer(Modifier.width(11.dp))
            BasicTextField(
                value = query,
                onValueChange = onQuery,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = colors.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(colors.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSubmit(query) }),
                decorationBox = { field ->
                    Box {
                        if (query.isBlank()) {
                            Text(placeholder, color = colors.onSurfaceVariant,
                                fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        field()
                    }
                }
            )
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQuery("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Borrar", tint = colors.onSurfaceVariant)
                }
            }
            IconButton(onClick = { onSubmit(query) }) {
                Icon(Icons.Rounded.ArrowForward, contentDescription = "Buscar", tint = colors.primary)
            }
        }
        if (recognitionAvailable) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.primaryContainer)
                    .clickable(onClick = if (recognitionBusy) onCancelRecognition else onRecognize)
                    .padding(horizontal = 15.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = colors.onPrimaryContainer)
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text("AURIA IDENTIFY", color = colors.onPrimaryContainer,
                        fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Text(if (recognitionBusy) "Cancelar reconocimiento" else "Reconocer música de otro lugar",
                        color = colors.onPrimaryContainer.copy(alpha = .85f), fontSize = 11.sp)
                }
                Icon(Icons.Rounded.ArrowForward, contentDescription = null, tint = colors.onPrimaryContainer)
            }
        }
    }
}

@Composable
internal fun AuriaSearchDiscovery(
    tracks: List<Track>,
    onPlay: (Track) -> Unit,
    onRemove: (Track) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("01", color = colors.primary, fontWeight = FontWeight.Black, fontSize = 12.sp)
            Box(Modifier.size(width = 2.dp, height = 26.dp).background(colors.primary))
            Text("ESCUCHADO ANTES", color = colors.onSurface,
                fontSize = 20.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            Text(tracks.size.toString(), color = colors.onSurfaceVariant, fontSize = 12.sp)
        }
        tracks.take(8).forEachIndexed { index, track ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(colors.surfaceContainer)
                    .clickable(onClick = { onPlay(track) })
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text((index + 1).toString().padStart(2, '0'), color = colors.primary,
                    fontSize = 12.sp, fontWeight = FontWeight.Black)
                AsyncImage(
                    model = track.thumbnailUrl.ifBlank { track.largeThumbnailUrl },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(45.dp).clip(RoundedCornerShape(6.dp))
                )
                Column(Modifier.weight(1f)) {
                    Text(track.title, color = colors.onSurface, fontWeight = FontWeight.Bold,
                        fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(track.artist, color = colors.onSurfaceVariant, fontSize = 11.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = { onRemove(track) }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Rounded.Close, contentDescription = "Quitar", tint = colors.onSurfaceVariant)
                }
            }
        }
        if (tracks.isEmpty()) {
            Text("Tu próxima canción puede estar aquí.", color = colors.onSurfaceVariant,
                fontSize = 13.sp, modifier = Modifier.padding(vertical = 20.dp))
        }
    }
}
