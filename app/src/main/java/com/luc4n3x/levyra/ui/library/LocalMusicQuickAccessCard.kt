package com.luc4n3x.levyra.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.luc4n3x.levyra.ui.i18n.LocalLevyraStrings

@Composable
internal fun LocalMusicQuickAccessCard(
    songCount: Int,
    hasPermission: Boolean,
    artworkModel: String,
    onOpen: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffleAll: () -> Unit
) {
    val strings = LocalLevyraStrings.current
    val colors = MaterialTheme.colorScheme
    Surface(
        color = colors.surfaceContainer,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (artworkModel.isNotBlank() && hasPermission) {
                    LibraryArtwork(
                        url = artworkModel,
                        title = strings.localOnDevice,
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(18.dp),
                        selected = false
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.LibraryMusic,
                        contentDescription = null,
                        tint = colors.onPrimaryContainer,
                        modifier = Modifier
                            .size(60.dp)
                            .background(colors.primaryContainer, RoundedCornerShape(16.dp))
                            .padding(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.localOnDevice,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when {
                            !hasPermission -> strings.localPermissionRequired
                            songCount > 0 -> strings.formatTrackCount(songCount)
                            else -> strings.localEmpty
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onOpen,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (hasPermission) strings.open else strings.localGrantPermission,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (songCount > 0 && hasPermission) {
                    OutlinedButton(onClick = onPlayAll, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(strings.playAll, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    IconButton(onClick = onShuffleAll, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Rounded.Shuffle, contentDescription = strings.shuffle)
                    }
                }
            }
        }
    }
}
