package com.luc4n3x.levyra.data.locallibrary

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import com.luc4n3x.levyra.domain.Track
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal fun isAuriaLocalFavoriteSource(source: String, streamUrl: String): Boolean =
    source == LOCAL_MEDIA_SOURCE && streamUrl.startsWith("content://")

internal data class AuriaFavoriteCopyResult(
    val copied: Int,
    val existing: Int,
    val unavailable: Int,
    val failed: Int
)

internal object AuriaFavoriteFileExporter {
    suspend fun copyToFolder(
        context: Context,
        folder: Uri,
        favorites: List<Track>
    ): AuriaFavoriteCopyResult = withContext(Dispatchers.IO) {
        val resolver = context.applicationContext.contentResolver
        var copied = 0
        var existing = 0
        var unavailable = 0
        var failed = 0
        val target = try {
            DocumentsContract.buildDocumentUriUsingTree(folder, DocumentsContract.getTreeDocumentId(folder))
        } catch (_: IllegalArgumentException) {
            return@withContext AuriaFavoriteCopyResult(0, 0, 0, favorites.size)
        }
        val names = readNames(resolver, folder)
            ?: return@withContext AuriaFavoriteCopyResult(0, 0, 0, favorites.size)
        val unique = favorites.distinctBy { it.streamUrl }
        for (track in unique) {
            currentCoroutineContext().ensureActive()
            val source = track.streamUrl
            if (!isAuriaLocalFavoriteSource(track.source, source)) {
                unavailable++
                continue
            }
            val sourceUri = runCatching { Uri.parse(source) }.getOrNull()
            if (sourceUri == null) {
                unavailable++
                continue
            }
            val fileName = sourceName(resolver, sourceUri)
            if (fileName == null) {
                unavailable++
                continue
            }
            if (fileName in names) {
                existing++
                continue
            }
            val type = runCatching { resolver.getType(sourceUri) }.getOrNull()?.takeIf { it.startsWith("audio/") }
                ?: guessMime(fileName)
            var created: Uri? = null
            try {
                val destinationUri = DocumentsContract.createDocument(resolver, target, type, fileName)
                    ?: throw IOException("Cannot create destination document")
                created = destinationUri
                val expectedSize = sourceSize(resolver, sourceUri)
                var written = 0L
                val input = resolver.openInputStream(sourceUri) ?: throw IOException("Cannot read source")
                input.use { original ->
                    val output = resolver.openOutputStream(destinationUri, "w")
                        ?: throw IOException("Cannot write destination")
                    output.use { destination ->
                        val buffer = ByteArray(64 * 1024)
                        while (true) {
                            currentCoroutineContext().ensureActive()
                            val length = original.read(buffer)
                            if (length < 0) break
                            destination.write(buffer, 0, length)
                            written += length
                        }
                        destination.flush()
                    }
                }
                if (written == 0L || (expectedSize != null && expectedSize != written)) {
                    throw IOException("Copy size mismatch")
                }
                val savedSize = documentSize(resolver, destinationUri)
                if (savedSize != null && savedSize != written) {
                    throw IOException("Destination size mismatch")
                }
                names.add(fileName)
                copied++
            } catch (cancel: CancellationException) {
                created?.let { runCatching { DocumentsContract.deleteDocument(resolver, it) } }
                throw cancel
            } catch (_: Exception) {
                created?.let { runCatching { DocumentsContract.deleteDocument(resolver, it) } }
                failed++
            }
        }
        AuriaFavoriteCopyResult(copied, existing, unavailable, failed)
    }

    private fun readNames(resolver: ContentResolver, folder: Uri): MutableSet<String>? {
        return try {
            val children = DocumentsContract.buildChildDocumentsUriUsingTree(
                folder, DocumentsContract.getTreeDocumentId(folder)
            )
            val names = mutableSetOf<String>()
            val cursor = resolver.query(
                children, arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME), null, null, null
            ) ?: return null
            cursor.use {
                while (it.moveToNext()) {
                    it.getString(0)?.let(names::add)
                }
            }
            names
        } catch (_: Exception) {
            null
        }
    }

    private fun sourceName(resolver: ContentResolver, uri: Uri): String? {
        val fromProvider = try {
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
                if (it.moveToFirst()) it.getString(0) else null
            }
        } catch (_: Exception) {
            null
        }
        return fromProvider?.trim()
            ?.takeIf { it.isNotBlank() && it != "." && it != ".." && !it.contains('/') && !it.contains('\\') }
    }

    private fun sourceSize(resolver: ContentResolver, uri: Uri): Long? = try {
        resolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use {
            if (it.moveToFirst() && !it.isNull(0)) it.getLong(0).takeIf { size -> size >= 0L } else null
        }
    } catch (_: Exception) {
        null
    }

    private fun documentSize(resolver: ContentResolver, uri: Uri): Long? = try {
        resolver.query(uri, arrayOf(DocumentsContract.Document.COLUMN_SIZE), null, null, null)?.use {
            if (it.moveToFirst() && !it.isNull(0)) it.getLong(0).takeIf { size -> size >= 0L } else null
        }
    } catch (_: Exception) {
        null
    }

    private fun guessMime(name: String): String = when (name.substringAfterLast('.', "").lowercase()) {
        "mp3" -> "audio/mpeg"
        "m4a", "mp4" -> "audio/mp4"
        "flac" -> "audio/flac"
        "ogg", "opus" -> "audio/ogg"
        "wav" -> "audio/wav"
        "aac" -> "audio/aac"
        else -> "application/octet-stream"
    }
}
