package com.luc4n3x.levyra.player

internal fun auriaDjForPlayback(
    manuallyDisabled: Boolean,
    loopingPlaylist: Boolean,
    videoMode: Boolean,
    liveRadio: Boolean
): Boolean = !manuallyDisabled && !loopingPlaylist && !videoMode && !liveRadio
