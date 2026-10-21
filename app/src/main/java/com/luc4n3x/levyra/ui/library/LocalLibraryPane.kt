package com.luc4n3x.levyra.ui.library

import com.luc4n3x.levyra.ui.components.LevyraPlayAllIconButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luc4n3x.levyra.data.locallibrary.LocalAlbumGroup
import com.luc4n3x.levyra.data.locallibrary.LocalLibraryCatalog
import com.luc4n3x.levyra.data.locallibrary.LocalArtistGroup
import com.luc4n3x.levyra.data.local.LocalMediaEntity
import com.luc4n3x.levyra.data.locallibrary.LocalFolderGroup
import com.luc4n3x.levyra.data.locallibrary.LocalGenreGroup
import com.luc4n3x.levyra.data.locallibrary.isSafeTagEditorFormat
import com.luc4n3x.levyra.data.locallibrary.matchesFullTagQuery
import com.luc4n3x.levyra.domain.Track
import com.luc4n3x.levyra.ui.i18n.LevyraStrings
import com.luc4n3x.levyra.ui.i18n.LocalLevyraStrings
import com.luc4n3x.levyra.ui.i18n.localLibraryRecentFilterLabel
import com.luc4n3x.levyra.ui.theme.LevyraCyan
import com.luc4n3x.levyra.ui.theme.LevyraGlass
import com.luc4n3x.levyra.ui.theme.LevyraMuted
import com.luc4n3x.levyra.ui.theme.LevyraPanelSoft
import com.luc4n3x.levyra.ui.theme.LevyraText
import com.luc4n3x.levyra.viewmodel.LocalLibraryUiState
import java.util.Locale

internal enum class LocalLibraryTab { Songs, Albums, Artists, Genres, Folders }

internal enum class LocalLibraryQualityFilter { All, Lossless, HighBitrate, Recent }

internal data class LocalLibraryAlphabetTarget(
    val letter: Char,
    val contentIndex: Int
)

internal data class LocalLibraryAlphabetIndex(
    val targets: List<LocalLibraryAlphabetTarget> = emptyList(),
    val itemCount: Int = 0
) {
    val visible: Boolean
        get() = itemCount >= LOCAL_ALPHABET_MIN_ITEMS && targets.size >= LOCAL_ALPHABET_MIN_LETTERS
}

internal fun LocalLibraryTab.label(strings: LevyraStrings): String = when (this) {
    LocalLibraryTab.Songs -> strings.songsPlain
    LocalLibraryTab.Albums -> strings.albumsPlain
    LocalLibraryTab.Artists -> strings.artists
    LocalLibraryTab.Genres -> strings.genres
    LocalLibraryTab.Folders -> strings.localFolders
}

internal data class LocalLibraryCallbacks(
    val onPlay: (List<Track>, Track) -> Unit,
    val onAddToQueue: (List<Track>) -> Unit,
    val onPlayAll: (List<Track>) -> Unit,
    val onShuffleAll: (List<Track>) -> Unit,
    val onAddToPlaylist: (Track) -> Unit,
    val onToggleDj: (Boolean) -> Unit,
    val onToggleFavorite: (Track) -> Unit,
    val onQuickScan: () -> Unit,
    val onFullScan: () -> Unit,
    val onRebuildLevyra: () -> Unit,
    val onGrantPermission: () -> Unit,
    val onToggleFolderHidden: (String, Boolean) -> Unit,
    val onEditTags: (Track) -> Unit
)

internal fun LazyListScope.localLibrarySection(
    library: LocalLibraryUiState,
    tab: LocalLibraryTab,
    onTab: (LocalLibraryTab) -> Unit,
    djEnabled: Boolean,
    djMixEnabled: Boolean,
    qualityFilter: LocalLibraryQualityFilter,
    onQualityFilter: (LocalLibraryQualityFilter) -> Unit,
    nowMs: Long,
    expandedGroupKey: String?,
    onExpandGroup: (String?) -> Unit,
    query: String,
    currentTrack: Track?,
    isPlaying: Boolean,
    favoriteIds: Set<String>,
    unavailableUris: Set<String>,
    callbacks: LocalLibraryCallbacks
) {
    item(key = "local-actions", contentType = "local-actions") {
        LocalLibraryActions(library, callbacks, djEnabled, djMixEnabled)
    }
    if (!library.permissionGranted) {
        item(key = "local-permission", contentType = "local-permission") {
            LocalLibraryPermissionCard(callbacks.onGrantPermission)
        }
        return
    }
    item(key = "local-tabs", contentType = "local-tabs") {
        LocalLibraryTabs(
            catalog = library.catalog,
            selected = tab,
            onSelect = { entry ->
                onExpandGroup(null)
                onTab(entry)
            }
        )
    }
    if (library.catalog.totalCount == 0) {
        item(key = "local-empty", contentType = "local-empty") {
            LibraryEmpty(Icons.Rounded.LibraryMusic, LocalLevyraStrings.current.localEmpty)
        }
        return
    }
    val availableQualityFilters = localLibraryQualityFilters(
        library.catalog.mediaByUri.values,
        nowMs
    )
    val effectiveQualityFilter = qualityFilter.takeIf { it in availableQualityFilters }
        ?: LocalLibraryQualityFilter.All
    if (availableQualityFilters.size > 1) {
        item(key = "local-quality-filters", contentType = "local-quality-filters") {
            LocalLibraryQualityFilters(
                filters = availableQualityFilters,
                selected = effectiveQualityFilter,
                onSelect = onQualityFilter
            )
        }
    }
    when (tab) {
        LocalLibraryTab.Songs -> {
            if (query.isBlank() && effectiveQualityFilter == LocalLibraryQualityFilter.All && library.catalog.albums.isNotEmpty()) {
                item(key = "local-album-featured", contentType = "local-featured") {
                    LocalAlbumSpotlight(
                        albums = library.catalog.albums,
                        onOpenAll = { onTab(LocalLibraryTab.Albums) },
                        onOpenAlbum = { album ->
                            onExpandGroup(album.key)
                            onTab(LocalLibraryTab.Albums)
                        },
                        onPlayAlbum = { album ->
                            album.tracks.firstOrNull()?.let { callbacks.onPlay(album.tracks, it) }
                        }
                    )
                }
            }
            val songs = library.catalog.songs
                .filterLocalTracks(query, library.catalog.mediaByUri)
                .filterByLocalQuality(effectiveQualityFilter, library.catalog.mediaByUri, nowMs)
            localTrackItems(
                keyPrefix = "local-song",
                tracks = songs,
                context = songs,
                currentTrack = currentTrack,
                isPlaying = isPlaying,
                favoriteIds = favoriteIds,
                unavailableUris = unavailableUris,
                mediaByUri = library.catalog.mediaByUri,
                callbacks = callbacks
            )
        }
        LocalLibraryTab.Albums -> {
            val albums = library.catalog.albums
                .filterLocalAlbums(query, library.catalog.mediaByUri)
                .mapNotNull { it.filteredByLocalQuality(effectiveQualityFilter, library.catalog.mediaByUri, nowMs) }
            albums.forEach { album ->
                item(key = "local-album-${album.key}", contentType = "local-group") {
                    LocalGroupBlock(
                        title = album.title.ifBlank { LocalLevyraStrings.current.localUnknownAlbum },
                        subtitle = localGroupSubtitle(album.artist, album.tracks.size, album.year),
                        artworkModel = album.artworkModel,
                        icon = Icons.Rounded.Album,
                        expanded = expandedGroupKey == album.key,
                        onToggle = { onExpandGroup(if (expandedGroupKey == album.key) null else album.key) },
                        onPlay = { album.tracks.firstOrNull()?.let { callbacks.onPlay(album.tracks, it) } }
                    )
                }
                if (expandedGroupKey == album.key) {
                    localTrackItems(
                        keyPrefix = "local-album-expanded-${album.key}",
                        tracks = album.tracks,
                        context = album.tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        favoriteIds = favoriteIds,
                        unavailableUris = unavailableUris,
                        mediaByUri = library.catalog.mediaByUri,
                        callbacks = callbacks,
                        indented = true
                    )
                }
            }
        }
        LocalLibraryTab.Artists -> {
            val artists = library.catalog.artists
                .filterLocalArtists(query, library.catalog.mediaByUri)
                .mapNotNull { it.filteredByLocalQuality(effectiveQualityFilter, library.catalog.mediaByUri, nowMs) }
            artists.forEach { artist ->
                item(key = "local-artist-${artist.key}", contentType = "local-group") {
                    val strings = LocalLevyraStrings.current
                    LocalGroupBlock(
                        title = artist.name.ifBlank { strings.localUnknownArtist },
                        subtitle = localGroupSubtitle(
                            "${artist.albumCount} ${strings.albumsPlain}",
                            artist.tracks.size,
                            ""
                        ),
                        artworkModel = artist.artworkModel,
                        icon = Icons.Rounded.Person,
                        expanded = expandedGroupKey == artist.key,
                        onToggle = { onExpandGroup(if (expandedGroupKey == artist.key) null else artist.key) },
                        onPlay = { artist.tracks.firstOrNull()?.let { callbacks.onPlay(artist.tracks, it) } }
                    )
                }
                if (expandedGroupKey == artist.key) {
                    localTrackItems(
                        keyPrefix = "local-artist-expanded-${artist.key}",
                        tracks = artist.tracks,
                        context = artist.tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        favoriteIds = favoriteIds,
                        unavailableUris = unavailableUris,
                        mediaByUri = library.catalog.mediaByUri,
                        callbacks = callbacks,
                        indented = true
                    )
                }
            }
        }
        LocalLibraryTab.Genres -> {
            val genres = library.catalog.genres
                .filterLocalGenres(query, library.catalog.mediaByUri)
                .mapNotNull { it.filteredByLocalQuality(effectiveQualityFilter, library.catalog.mediaByUri, nowMs) }
            genres.forEach { genre ->
                item(key = "local-genre-${genre.key}", contentType = "local-group") {
                    LocalGroupBlock(
                        title = genre.name,
                        subtitle = LocalLevyraStrings.current.formatTrackCount(genre.tracks.size),
                        artworkModel = genre.artworkModel,
                        icon = Icons.Rounded.Category,
                        expanded = expandedGroupKey == genre.key,
                        onToggle = { onExpandGroup(if (expandedGroupKey == genre.key) null else genre.key) },
                        onPlay = { genre.tracks.firstOrNull()?.let { callbacks.onPlay(genre.tracks, it) } }
                    )
                }
                if (expandedGroupKey == genre.key) {
                    localTrackItems(
                        keyPrefix = "local-genre-expanded-${genre.key}",
                        tracks = genre.tracks,
                        context = genre.tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        favoriteIds = favoriteIds,
                        unavailableUris = unavailableUris,
                        mediaByUri = library.catalog.mediaByUri,
                        callbacks = callbacks,
                        indented = true
                    )
                }
            }
        }
        LocalLibraryTab.Folders -> {
            val folders = library.catalog.folders
                .filterLocalFolders(query, library.catalog.mediaByUri)
                .mapNotNull { it.filteredByLocalQuality(effectiveQualityFilter, library.catalog.mediaByUri, nowMs) }
            folders.forEach { folder ->
                item(key = "local-folder-${folder.key}", contentType = "local-group") {
                    LocalGroupBlock(
                        title = folder.name.ifBlank { folder.volumeName },
                        subtitle = localGroupSubtitle(folder.path, folder.tracks.size, ""),
                        artworkModel = folder.tracks.firstOrNull()?.thumbnailUrl.orEmpty(),
                        icon = Icons.Rounded.Folder,
                        expanded = expandedGroupKey == folder.key,
                        onToggle = { onExpandGroup(if (expandedGroupKey == folder.key) null else folder.key) },
                        onPlay = { folder.tracks.firstOrNull()?.let { callbacks.onPlay(folder.tracks, it) } },
                        onHide = { callbacks.onToggleFolderHidden(folder.key, true) }
                    )
                }
                if (expandedGroupKey == folder.key) {
                    localTrackItems(
                        keyPrefix = "local-folder-expanded-${folder.key}",
                        tracks = folder.tracks,
                        context = folder.tracks,
                        currentTrack = currentTrack,
                        isPlaying = isPlaying,
                        favoriteIds = favoriteIds,
                        unavailableUris = unavailableUris,
                        mediaByUri = library.catalog.mediaByUri,
                        callbacks = callbacks,
                        indented = true
                    )
                }
            }
        }
    }
    if (library.catalog.hiddenDuplicateCount > 0) {
        item(key = "local-duplicates", contentType = "local-note") {
            Text(
                text = "${LocalLevyraStrings.current.localDuplicatesHidden} · ${library.catalog.hiddenDuplicateCount}",
                color = LevyraMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

private fun LazyListScope.localTrackItems(
    keyPrefix: String,
    tracks: List<Track>,
    context: List<Track>,
    currentTrack: Track?,
    isPlaying: Boolean,
    favoriteIds: Set<String>,
    unavailableUris: Set<String>,
    mediaByUri: Map<String, LocalMediaEntity>,
    callbacks: LocalLibraryCallbacks,
    indented: Boolean = false
) {
    items(tracks, key = { "$keyPrefix-${it.streamUrl}" }, contentType = { "local-track" }) { track ->
        LibraryTrackRow(
            track = track,
            selected = false,
            selectionActive = false,
            isCurrent = currentTrack?.streamUrl == track.streamUrl,
            isPlaying = isPlaying && currentTrack?.streamUrl == track.streamUrl,
            isFavorite = track.id in favoriteIds,
            isDownloaded = true,
            downloadProgress = null,
            metadata = if (track.streamUrl in unavailableUris) {
                LocalLevyraStrings.current.localFileUnavailable
            } else {
                mediaByUri[track.streamUrl]?.localAudioSummary()
            },
            onClick = { callbacks.onPlay(context, track) },
            onLongClick = { callbacks.onAddToQueue(listOf(track)) },
            onFavorite = { callbacks.onToggleFavorite(track) },
            onDownload = {},
            onQueue = { callbacks.onAddToQueue(listOf(track)) },
            onAddToPlaylist = { callbacks.onAddToPlaylist(track) },
            onEditTags = mediaByUri[track.streamUrl]
                ?.takeIf { it.isSafeTagEditorFormat() }
                ?.let { { callbacks.onEditTags(track) } },
            modifier = if (indented) Modifier.padding(start = 16.dp) else Modifier
        )
    }
}

@Composable
private fun LocalAlbumSpotlight(
    albums: List<LocalAlbumGroup>,
    onOpenAll: () -> Unit,
    onOpenAlbum: (LocalAlbumGroup) -> Unit,
    onPlayAlbum: (LocalAlbumGroup) -> Unit
) {
    val strings = LocalLevyraStrings.current
    val colors = MaterialTheme.colorScheme
    val featured = remember(albums) {
        albums.asSequence()
            .filter { it.tracks.isNotEmpty() }
            .sortedByDescending { it.tracks.size }
            .take(8)
            .toList()
    }
    if (featured.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                strings.albumsPlain,
                color = colors.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(onClick = onOpenAll, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(strings.open, maxLines = 1)
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(featured, key = { it.key }) { album ->
                Surface(
                    onClick = { onOpenAlbum(album) },
                    shape = RoundedCornerShape(20.dp),
                    color = colors.surfaceContainerLow,
                    modifier = Modifier.width(164.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        if (album.artworkModel.isNotBlank()) {
                            LibraryArtwork(
                                url = album.artworkModel,
                                title = album.title,
                                modifier = Modifier.fillMaxWidth().height(164.dp),
                                shape = RoundedCornerShape(20.dp),
                                selected = false
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(164.dp)
                                    .background(colors.secondaryContainer, RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Album,
                                    contentDescription = null,
                                    modifier = Modifier.size(44.dp),
                                    tint = colors.onSecondaryContainer
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 4.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    album.title.ifBlank { strings.localUnknownAlbum },
                                    color = colors.onSurface,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    album.artist.ifBlank { strings.localUnknownArtist },
                                    color = colors.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = { onPlayAlbum(album) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.PlayArrow,
                                    contentDescription = strings.playAll,
                                    tint = colors.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocalLibraryTabs(
    catalog: LocalLibraryCatalog,
    selected: LocalLibraryTab,
    onSelect: (LocalLibraryTab) -> Unit
) {
    val strings = LocalLevyraStrings.current
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocalLibraryTab.entries.forEach { entry ->
            val (icon, count) = when (entry) {
                LocalLibraryTab.Songs -> Icons.Rounded.MusicNote to catalog.totalCount
                LocalLibraryTab.Albums -> Icons.Rounded.Album to catalog.albums.size
                LocalLibraryTab.Artists -> Icons.Rounded.Person to catalog.artists.size
                LocalLibraryTab.Genres -> Icons.Rounded.Category to catalog.genres.size
                LocalLibraryTab.Folders -> Icons.Rounded.Folder to catalog.folders.size
            }
            val active = selected == entry
            Surface(
                onClick = { onSelect(entry) },
                shape = RoundedCornerShape(18.dp),
                color = if (active) colors.secondaryContainer else colors.surfaceContainerLow,
                contentColor = if (active) colors.onSecondaryContainer else colors.onSurface,
                modifier = Modifier.width(106.dp).heightIn(min = 94.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = entry.label(strings),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalLibraryQualityFilters(
    filters: List<LocalLibraryQualityFilter>,
    selected: LocalLibraryQualityFilter,
    onSelect: (LocalLibraryQualityFilter) -> Unit
) {
    val strings = LocalLevyraStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEach { filter ->
            val active = filter == selected
            val label = when (filter) {
                LocalLibraryQualityFilter.All -> strings.all
                LocalLibraryQualityFilter.Lossless -> "LOSSLESS"
                LocalLibraryQualityFilter.HighBitrate -> "320K+"
                LocalLibraryQualityFilter.Recent -> strings.localLibraryRecentFilterLabel()
            }
            FilterChip(
                selected = active,
                onClick = { onSelect(filter) },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                shapes = FilterChipDefaults.shapes(
                    shape = MaterialTheme.shapes.small,
                    selectedShape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.medium
                ),
                colors = FilterChipDefaults.tonalFilterChipColors(),
                modifier = Modifier.heightIn(min = 48.dp)
            )
        }
    }
}

internal fun localLibraryQualityFilters(
    media: Collection<LocalMediaEntity>,
    nowMs: Long
): List<LocalLibraryQualityFilter> {
    var hasLossless = false
    var hasHighBitrate = false
    var hasRecent = false
    val recentThreshold = nowMs - LOCAL_RECENT_WINDOW_MS
    for (track in media) {
        if (!hasLossless && track.isLosslessLocalMedia()) hasLossless = true
        if (!hasHighBitrate && track.bitrate >= LOCAL_HIGH_BITRATE_BPS) hasHighBitrate = true
        if (!hasRecent && track.dateAddedMs >= recentThreshold) hasRecent = true
        if (hasLossless && hasHighBitrate && hasRecent) break
    }
    return buildList {
        add(LocalLibraryQualityFilter.All)
        if (hasLossless) add(LocalLibraryQualityFilter.Lossless)
        if (hasHighBitrate) add(LocalLibraryQualityFilter.HighBitrate)
        if (hasRecent) add(LocalLibraryQualityFilter.Recent)
    }
}

internal fun LocalMediaEntity.matchesLocalQualityFilter(
    filter: LocalLibraryQualityFilter,
    nowMs: Long = System.currentTimeMillis()
): Boolean = when (filter) {
    LocalLibraryQualityFilter.All -> true
    LocalLibraryQualityFilter.Lossless -> isLosslessLocalMedia()
    LocalLibraryQualityFilter.HighBitrate -> bitrate >= LOCAL_HIGH_BITRATE_BPS
    LocalLibraryQualityFilter.Recent -> dateAddedMs >= nowMs - LOCAL_RECENT_WINDOW_MS
}

private fun LocalMediaEntity.isLosslessLocalMedia(): Boolean {
    val mime = mimeType.lowercase()
    val extension = displayName.substringAfterLast('.', "").lowercase()
    return mime.contains("flac") ||
        mime.contains("alac") ||
        mime.contains("wav") ||
        mime.contains("aiff") ||
        mime.contains("ape") ||
        extension in LOCAL_LOSSLESS_EXTENSIONS
}

private fun List<Track>.filterByLocalQuality(
    filter: LocalLibraryQualityFilter,
    mediaByUri: Map<String, LocalMediaEntity>,
    nowMs: Long
): List<Track> {
    if (filter == LocalLibraryQualityFilter.All) return this
    return filter { track -> mediaByUri[track.streamUrl]?.matchesLocalQualityFilter(filter, nowMs) == true }
}

private fun LocalAlbumGroup.filteredByLocalQuality(
    filter: LocalLibraryQualityFilter,
    mediaByUri: Map<String, LocalMediaEntity>,
    nowMs: Long
): LocalAlbumGroup? {
    val filtered = tracks.filterByLocalQuality(filter, mediaByUri, nowMs)
    if (filtered.isEmpty()) return null
    return copy(
        artworkModel = filtered.firstOrNull()?.thumbnailUrl.orEmpty(),
        durationMs = filtered.sumOf { it.durationMs.coerceAtLeast(0L) },
        tracks = filtered
    )
}

private fun LocalArtistGroup.filteredByLocalQuality(
    filter: LocalLibraryQualityFilter,
    mediaByUri: Map<String, LocalMediaEntity>,
    nowMs: Long
): LocalArtistGroup? {
    val filtered = tracks.filterByLocalQuality(filter, mediaByUri, nowMs)
    if (filtered.isEmpty()) return null
    val filteredAlbumCount = filtered
        .map { it.album.trim().lowercase() }
        .filter(String::isNotEmpty)
        .distinct()
        .size
    return copy(
        artworkModel = filtered.firstOrNull()?.thumbnailUrl.orEmpty(),
        albumCount = filteredAlbumCount.takeIf { it > 0 } ?: albumCount,
        tracks = filtered
    )
}

private fun LocalFolderGroup.filteredByLocalQuality(
    filter: LocalLibraryQualityFilter,
    mediaByUri: Map<String, LocalMediaEntity>,
    nowMs: Long
): LocalFolderGroup? {
    val filtered = tracks.filterByLocalQuality(filter, mediaByUri, nowMs)
    if (filtered.isEmpty()) return null
    return copy(
        durationMs = filtered.sumOf { it.durationMs.coerceAtLeast(0L) },
        tracks = filtered
    )
}

private fun LocalMediaEntity.localAudioSummary(): String? {
    val extension = displayName.substringAfterLast('.', "").uppercase(Locale.ROOT)
    val format = when {
        extension.isNotBlank() -> extension
        mimeType.contains("mpeg", ignoreCase = true) -> "MP3"
        mimeType.contains("flac", ignoreCase = true) -> "FLAC"
        mimeType.contains("wav", ignoreCase = true) -> "WAV"
        mimeType.contains("ogg", ignoreCase = true) -> "OGG"
        mimeType.contains("opus", ignoreCase = true) -> "OPUS"
        else -> mimeType.substringAfter('/').substringBefore(';').uppercase(Locale.ROOT)
    }
    val bitrateLabel = bitrate
        .takeIf { it > 0 }
        ?.div(1_000)
        ?.let { "$it kbps" }
    return listOf(format, bitrateLabel)
        .filterNotNull()
        .filter(String::isNotBlank)
        .joinToString(" · ")
        .takeIf(String::isNotBlank)
}

private const val LOCAL_HIGH_BITRATE_BPS = 320_000
private const val LOCAL_RECENT_WINDOW_MS = 30L * 24L * 60L * 60L * 1_000L
private val LOCAL_LOSSLESS_EXTENSIONS = setOf("flac", "alac", "wav", "wave", "aiff", "aif", "ape")

internal fun localLibraryLeadingItemCount(
    library: LocalLibraryUiState,
    nowMs: Long,
    tab: LocalLibraryTab = LocalLibraryTab.Songs,
    query: String = "",
    qualityFilter: LocalLibraryQualityFilter = LocalLibraryQualityFilter.All
): Int {
    if (!library.permissionGranted || library.catalog.totalCount == 0) return 0
    val available = localLibraryQualityFilters(library.catalog.mediaByUri.values, nowMs)
    val showsFeaturedAlbums = tab == LocalLibraryTab.Songs &&
        query.isBlank() &&
        (qualityFilter == LocalLibraryQualityFilter.All || qualityFilter !in available) &&
        library.catalog.albums.isNotEmpty()
    return 2 + (if (available.size > 1) 1 else 0) + (if (showsFeaturedAlbums) 1 else 0)
}

internal fun localLibraryAlphabetIndex(
    library: LocalLibraryUiState,
    tab: LocalLibraryTab,
    qualityFilter: LocalLibraryQualityFilter,
    query: String,
    nowMs: Long,
    songsAlphabetical: Boolean
): LocalLibraryAlphabetIndex {
    if (
        !library.permissionGranted ||
        library.catalog.totalCount == 0 ||
        tab == LocalLibraryTab.Folders ||
        tab == LocalLibraryTab.Songs && !songsAlphabetical
    ) {
        return LocalLibraryAlphabetIndex()
    }
    val availableFilters = localLibraryQualityFilters(library.catalog.mediaByUri.values, nowMs)
    val effectiveFilter = qualityFilter.takeIf { it in availableFilters } ?: LocalLibraryQualityFilter.All
    val labels = when (tab) {
        LocalLibraryTab.Songs -> library.catalog.songs
            .filterLocalTracks(query, library.catalog.mediaByUri)
            .filterByLocalQuality(effectiveFilter, library.catalog.mediaByUri, nowMs)
            .map(Track::title)
        LocalLibraryTab.Albums -> library.catalog.albums
            .filterLocalAlbums(query, library.catalog.mediaByUri)
            .mapNotNull { it.filteredByLocalQuality(effectiveFilter, library.catalog.mediaByUri, nowMs) }
            .map(LocalAlbumGroup::title)
        LocalLibraryTab.Artists -> library.catalog.artists
            .filterLocalArtists(query, library.catalog.mediaByUri)
            .mapNotNull { it.filteredByLocalQuality(effectiveFilter, library.catalog.mediaByUri, nowMs) }
            .map(LocalArtistGroup::name)
        LocalLibraryTab.Genres -> library.catalog.genres
            .filterLocalGenres(query, library.catalog.mediaByUri)
            .mapNotNull { it.filteredByLocalQuality(effectiveFilter, library.catalog.mediaByUri, nowMs) }
            .map(LocalGenreGroup::name)
        LocalLibraryTab.Folders -> emptyList()
    }
    val targets = buildList {
        var previous: Char? = null
        labels.forEachIndexed { index, label ->
            val letter = localAlphabetLetter(label) ?: return@forEachIndexed
            if (letter != previous) {
                add(LocalLibraryAlphabetTarget(letter, index))
                previous = letter
            }
        }
    }
    return LocalLibraryAlphabetIndex(targets = targets, itemCount = labels.size)
}

@Composable
internal fun LocalLibraryAlphabetRail(
    index: LocalLibraryAlphabetIndex,
    activeLetter: Char?,
    onJump: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!index.visible) return
    fun jumpAt(y: Float, height: Int) {
        if (height <= 0 || index.targets.isEmpty()) return
        val slot = height.toFloat() / index.targets.size
        val targetIndex = (y / slot).toInt().coerceIn(0, index.targets.lastIndex)
        onJump(index.targets[targetIndex].contentIndex)
    }
    Surface(
        color = LevyraPanelSoft.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, LevyraMuted.copy(alpha = 0.14f)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .width(28.dp)
            .pointerInput(index.targets) {
                detectDragGestures(
                    onDragStart = { offset -> jumpAt(offset.y, size.height) },
                    onDrag = { change, _ ->
                        change.consume()
                        jumpAt(change.position.y, size.height)
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            index.targets.forEach { target ->
                val active = target.letter == activeLetter
                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height(14.dp)
                        .clickable { onJump(target.contentIndex) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = target.letter.toString(),
                        color = if (active) LevyraCyan else LevyraMuted,
                        fontSize = 8.5.sp,
                        fontWeight = if (active) FontWeight.Black else FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun localAlphabetLetter(value: String): Char? = normalizeLibraryText(value)
    .trimStart()
    .firstOrNull()
    ?.takeIf { it in 'a'..'z' }
    ?.uppercaseChar()

private const val LOCAL_ALPHABET_MIN_ITEMS = 50
private const val LOCAL_ALPHABET_MIN_LETTERS = 5

@Composable
private fun LocalLibraryActions(
    library: LocalLibraryUiState,
    callbacks: LocalLibraryCallbacks,
    djEnabled: Boolean,
    djMixEnabled: Boolean
) {
    val strings = LocalLevyraStrings.current
    val colors = MaterialTheme.colorScheme
    var scanMenuExpanded by remember { mutableStateOf(false) }
    Surface(
        color = colors.surfaceContainer,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(48.dp)
                        .background(colors.primaryContainer, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.LibraryMusic,
                        contentDescription = null,
                        tint = colors.onPrimaryContainer,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        strings.localOnDevice,
                        color = colors.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = library.message.ifBlank {
                            if (library.catalog.totalCount > 0) {
                                strings.formatTrackCount(library.catalog.totalCount)
                            } else {
                                strings.localOnDeviceSubtitle
                            }
                        },
                        color = colors.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (library.scanning) {
                    CircularProgressIndicator(
                        color = colors.primary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    IconButton(onClick = callbacks.onQuickScan) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = strings.localScanQuick,
                            tint = colors.primary
                        )
                    }
                }
            }
            if (library.catalog.songs.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { callbacks.onPlayAll(library.catalog.songs) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(strings.playAll, modifier = Modifier.padding(start = 6.dp), maxLines = 1)
                    }
                    OutlinedButton(
                        onClick = { callbacks.onShuffleAll(library.catalog.songs) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Shuffle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(strings.shuffle, modifier = Modifier.padding(start = 6.dp), maxLines = 1)
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = djEnabled,
                    onClick = { callbacks.onToggleDj(!djEnabled) },
                    label = { Text("AURIA DJ", fontWeight = FontWeight.SemiBold) },
                    leadingIcon = {
                        Icon(Icons.Rounded.GraphicEq, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier.heightIn(min = 48.dp)
                )
                if (djEnabled && djMixEnabled) {
                    Text(
                        "AutoMix",
                        color = colors.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    IconButton(onClick = { scanMenuExpanded = true }) {
                        Icon(
                            Icons.Rounded.MoreVert,
                            contentDescription = "${strings.localScanFull}, ${strings.localRebuildLevyra}",
                            tint = colors.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = scanMenuExpanded,
                        onDismissRequest = { scanMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(strings.localScanFull) },
                            onClick = {
                                scanMenuExpanded = false
                                callbacks.onFullScan()
                            },
                            leadingIcon = {
                                Icon(Icons.Rounded.Refresh, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.localRebuildLevyra) },
                            onClick = {
                                scanMenuExpanded = false
                                callbacks.onRebuildLevyra()
                            },
                            leadingIcon = {
                                Icon(Icons.Rounded.LibraryMusic, contentDescription = null)
                            }
                        )
                    }
                }
            }
            Text(
                text = strings.continuousRadioSubtitle,
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LocalLibraryPermissionCard(onGrant: () -> Unit) {
    val strings = LocalLevyraStrings.current
    Surface(
        color = LevyraPanelSoft,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onGrant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.Lock, contentDescription = null, tint = LevyraCyan, modifier = Modifier.size(22.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    strings.localPermissionRequired,
                    color = LevyraText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(strings.localGrantPermission, color = LevyraCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LocalGroupBlock(
    title: String,
    subtitle: String,
    artworkModel: String,
    icon: ImageVector,
    expanded: Boolean,
    onToggle: () -> Unit,
    onPlay: () -> Unit,
    onHide: (() -> Unit)? = null
) {
    val strings = LocalLevyraStrings.current
    val colors = MaterialTheme.colorScheme
    Surface(
        color = if (expanded) colors.secondaryContainer else colors.surfaceContainerLow,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (artworkModel.isNotBlank()) {
                LibraryArtwork(
                    url = artworkModel,
                    title = title,
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    selected = false
                )
            } else {
                Box(
                    modifier = Modifier.size(56.dp)
                        .background(colors.surfaceContainerHigh, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(24.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = colors.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (onHide != null) {
                IconButton(onClick = onHide, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Rounded.VisibilityOff,
                        strings.localHideFolder,
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            LevyraPlayAllIconButton(onClick = onPlay, size = 48.dp)
            Icon(
                imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

private fun localGroupSubtitle(detail: String, trackCount: Int, year: String): String = listOf(
    detail.trim(),
    "$trackCount",
    year.trim()
).filter { it.isNotEmpty() }.joinToString(" · ")

internal fun List<Track>.filterLocalTracks(
    query: String,
    mediaByUri: Map<String, LocalMediaEntity>
): List<Track> {
    val clean = normalizeLibraryText(query)
    if (clean.isEmpty()) return this
    return filter { track ->
        mediaByUri[track.streamUrl]?.matchesFullTagQuery(query) == true ||
            normalizeLibraryText(track.title).contains(clean) ||
            normalizeLibraryText(track.artist).contains(clean) ||
            normalizeLibraryText(track.album).contains(clean)
    }
}

internal fun List<LocalAlbumGroup>.filterLocalAlbums(
    query: String,
    mediaByUri: Map<String, LocalMediaEntity>
): List<LocalAlbumGroup> {
    val clean = normalizeLibraryText(query)
    if (clean.isEmpty()) return this
    return filter { group ->
        normalizeLibraryText(group.title).contains(clean) ||
            normalizeLibraryText(group.artist).contains(clean) ||
            group.tracks.any { mediaByUri[it.streamUrl]?.matchesFullTagQuery(query) == true }
    }
}

internal fun List<LocalArtistGroup>.filterLocalArtists(
    query: String,
    mediaByUri: Map<String, LocalMediaEntity>
): List<LocalArtistGroup> {
    val clean = normalizeLibraryText(query)
    if (clean.isEmpty()) return this
    return filter { group ->
        normalizeLibraryText(group.name).contains(clean) ||
            group.tracks.any { mediaByUri[it.streamUrl]?.matchesFullTagQuery(query) == true }
    }
}

private fun LocalGenreGroup.filteredByLocalQuality(
    filter: LocalLibraryQualityFilter,
    mediaByUri: Map<String, LocalMediaEntity>,
    nowMs: Long
): LocalGenreGroup? {
    val visible = tracks.filterByLocalQuality(filter, mediaByUri, nowMs)
    return takeIf { visible.isNotEmpty() }?.copy(tracks = visible)
}

internal fun List<LocalGenreGroup>.filterLocalGenres(
    query: String,
    mediaByUri: Map<String, LocalMediaEntity>
): List<LocalGenreGroup> {
    val clean = normalizeLibraryText(query)
    if (clean.isEmpty()) return this
    return filter { group ->
        normalizeLibraryText(group.name).contains(clean) ||
            group.tracks.any { mediaByUri[it.streamUrl]?.matchesFullTagQuery(query) == true }
    }
}

internal fun List<LocalFolderGroup>.filterLocalFolders(
    query: String,
    mediaByUri: Map<String, LocalMediaEntity>
): List<LocalFolderGroup> {
    val clean = normalizeLibraryText(query)
    if (clean.isEmpty()) return this
    return filter { group ->
        normalizeLibraryText(group.name).contains(clean) ||
            normalizeLibraryText(group.path).contains(clean) ||
            group.tracks.any { mediaByUri[it.streamUrl]?.matchesFullTagQuery(query) == true }
    }
}
