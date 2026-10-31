package com.luc4n3x.levyra.ui.player

import com.luc4n3x.levyra.domain.PlayerVisualMode
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

internal fun auriaPlayerVisualMode(
    selected: PlayerVisualMode,
    mode: AuriaVisualMode?
): PlayerVisualMode = if (selected != PlayerVisualMode.Halo) selected else when (mode) {
    AuriaVisualMode.Flow -> PlayerVisualMode.Flow
    AuriaVisualMode.Nebula -> PlayerVisualMode.Halo
    AuriaVisualMode.Pulse -> PlayerVisualMode.Pulse
    AuriaVisualMode.Studio -> PlayerVisualMode.Folio
    null -> selected
}
