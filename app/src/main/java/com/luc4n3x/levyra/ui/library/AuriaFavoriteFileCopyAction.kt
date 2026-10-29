package com.luc4n3x.levyra.ui.library

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luc4n3x.levyra.data.locallibrary.AuriaFavoriteCopyResult
import com.luc4n3x.levyra.data.locallibrary.isAuriaLocalFavoriteSource
import com.luc4n3x.levyra.domain.Track
import com.luc4n3x.levyra.ui.i18n.LocalLevyraStrings

@Composable
internal fun AuriaFavoriteFileCopyAction(
    tracks: List<Track>,
    selectionActive: Boolean,
    running: Boolean,
    result: AuriaFavoriteCopyResult?,
    onDismissResult: () -> Unit,
    onCopy: (Uri, List<Track>) -> Unit
) {
    val strings = LocalLevyraStrings.current
    val copy = remember(strings.code) { AuriaFavoriteCopyCopy.forCode(strings.code) }
    var pending by remember { mutableStateOf<List<Track>>(emptyList()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { folder: Uri? ->
        val snapshot = pending
        pending = emptyList()
        if (folder == null || snapshot.isEmpty()) return@rememberLauncherForActivityResult
        onCopy(folder, snapshot)
    }
    val localTracks = tracks.filter { isAuriaLocalFavoriteSource(it.source, it.streamUrl) }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Button(
            onClick = {
                pending = localTracks.toList()
                launcher.launch(null)
            },
            enabled = localTracks.isNotEmpty() && !running,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Rounded.ContentCopy, contentDescription = null)
            Text(
                text = if (running) copy.working else if (selectionActive) {
                    copy.selected(localTracks.size)
                } else {
                    copy.all(localTracks.size)
                },
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Text(
            text = copy.subtitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
    result?.let { report ->
        AlertDialog(
            onDismissRequest = onDismissResult,
            title = { Text(copy.complete) },
            text = {
                Text(copy.summary(report))
            },
            confirmButton = {
                TextButton(onClick = onDismissResult) { Text(copy.done) }
            }
        )
    }
}

internal data class AuriaFavoriteCopyCopy(
    val all: (Int) -> String,
    val selected: (Int) -> String,
    val subtitle: String,
    val working: String,
    val complete: String,
    val done: String,
    val summary: (AuriaFavoriteCopyResult) -> String
) {
    companion object {
        fun forCode(code: String): AuriaFavoriteCopyCopy = when (code.substringBefore('-').lowercase()) {
            "es" -> AuriaFavoriteCopyCopy(
                all = { "Copiar $it canciones locales de Me gusta" },
                selected = { "Copiar $it canciones seleccionadas" },
                subtitle = "Elige una carpeta. Solo se copian archivos locales; los originales se conservan.",
                working = "Copiando canciones…",
                complete = "Copia finalizada",
                done = "Listo",
                summary = { "Copiadas: ${it.copied}\nYa existentes: ${it.existing}\nNo locales: ${it.unavailable}\nErrores: ${it.failed}" }
            )
            else -> AuriaFavoriteCopyCopy(
                all = { "Copy $it local liked songs" },
                selected = { "Copy $it selected local songs" },
                subtitle = "Choose a folder. Only local files are copied; originals are preserved.",
                working = "Copying songs…",
                complete = "Copy finished",
                done = "Done",
                summary = { "Copied: ${it.copied}\nAlready there: ${it.existing}\nNot local: ${it.unavailable}\nErrors: ${it.failed}" }
            )
        }
    }
}
