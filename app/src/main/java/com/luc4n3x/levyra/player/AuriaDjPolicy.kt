package com.luc4n3x.levyra.player

internal fun auriaDjForPlayback(
    existing: Boolean,
    manuallyDisabled: Boolean,
    loopingPlaylist: Boolean,
    videoMode: Boolean,
    liveRadio: Boolean
): Boolean {
    if (loopingPlaylist || videoMode || liveRadio) return false
    return existing || !manuallyDisabled
}
