package com.luc4n3x.levyra.ui

import com.luc4n3x.levyra.domain.MiniPlayerStyle
import com.luc4n3x.levyra.domain.NavStyle
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

internal fun auriaMiniStyle(mode: AuriaVisualMode?, stored: MiniPlayerStyle): MiniPlayerStyle =
    if (mode == null || stored != MiniPlayerStyle.Connected) stored else when (mode) {
        AuriaVisualMode.Flow, AuriaVisualMode.Nebula -> MiniPlayerStyle.Floating
        AuriaVisualMode.Pulse, AuriaVisualMode.Studio -> MiniPlayerStyle.UltraCompact
    }

internal fun auriaNavStyle(mode: AuriaVisualMode?, stored: NavStyle): NavStyle =
    if (mode == null || stored != NavStyle.ConnectedDock) stored else when (mode) {
        AuriaVisualMode.Flow, AuriaVisualMode.Nebula -> NavStyle.FloatingDock
        AuriaVisualMode.Pulse, AuriaVisualMode.Studio -> NavStyle.Minimal
    }
