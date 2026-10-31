package com.luc4n3x.levyra.ui.theme

enum class AuriaVisualMode(val themeId: String) {
    Flow(LevyraThemes.AURIA_FLOW),
    Nebula(LevyraThemes.AURIA_NEBULA),
    Pulse(LevyraThemes.AURIA_PULSE),
    Studio(LevyraThemes.AURIA_STUDIO);

    companion object {
        fun fromTheme(id: String): AuriaVisualMode? =
            if (id == LevyraThemes.AURIA_HALO) Flow else entries.firstOrNull { it.themeId == id }
    }
}
