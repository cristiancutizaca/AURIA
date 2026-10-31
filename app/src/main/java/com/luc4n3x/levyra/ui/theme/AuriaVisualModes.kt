package com.luc4n3x.levyra.ui.theme

enum class AuriaVisualMode(val themeId: String) {
    Flow(LevyraThemes.AURIA_FLOW),
    Nebula(LevyraThemes.AURIA_NEBULA),
    Pulse(LevyraThemes.AURIA_PULSE),
    Studio(LevyraThemes.AURIA_STUDIO);

    companion object {
        fun fromTheme(id: String): AuriaVisualMode? {
            entries.firstOrNull { it.themeId == id }?.let { return it }
            // Existing installations may still hold a preset from the old UI.
            // Map legacy built-ins to Flow visually without modifying stored data.
            return if (id == LevyraThemes.AURIA_HALO ||
                LevyraThemes.presets.any { it.id == id }
            ) Flow else null
        }
    }
}
