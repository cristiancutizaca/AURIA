package com.luc4n3x.levyra.ui

import com.luc4n3x.levyra.domain.HomeStyle
import com.luc4n3x.levyra.domain.MiniPlayerStyle
import com.luc4n3x.levyra.domain.NavStyle
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

/** AURIA modes override visual layouts only; old user preferences stay stored. */
internal fun auriaMiniStyle(mode: AuriaVisualMode?, stored: MiniPlayerStyle): MiniPlayerStyle =
    when (mode) {
        null -> stored
        AuriaVisualMode.Flow, AuriaVisualMode.Nebula -> MiniPlayerStyle.Floating
        AuriaVisualMode.Pulse, AuriaVisualMode.Studio -> MiniPlayerStyle.UltraCompact
    }

internal fun auriaNavStyle(mode: AuriaVisualMode?, stored: NavStyle): NavStyle =
    when (mode) {
        null -> stored
        AuriaVisualMode.Flow, AuriaVisualMode.Nebula -> NavStyle.FloatingDock
        AuriaVisualMode.Pulse, AuriaVisualMode.Studio -> NavStyle.Minimal
    }

internal fun auriaHomeStyle(mode: AuriaVisualMode?, stored: HomeStyle): HomeStyle =
    when (mode) {
        null -> stored
        AuriaVisualMode.Flow -> HomeStyle.Flow
        AuriaVisualMode.Nebula -> HomeStyle.Immersive
        AuriaVisualMode.Pulse -> HomeStyle.Minimal
        AuriaVisualMode.Studio -> HomeStyle.Editorial
    }
