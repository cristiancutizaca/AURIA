package com.luc4n3x.levyra.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuriaVisualModesTest {
    @Test fun fourStylesAreDistinctPersistentPresets() {
        assertEquals(4, AuriaVisualMode.entries.size)
        assertEquals(4, AuriaVisualMode.entries.map { it.themeId }.toSet().size)
        AuriaVisualMode.entries.forEach { mode ->
            assertEquals(mode, AuriaVisualMode.fromTheme(mode.themeId))
            assertTrue(LevyraThemes.presets.any { it.id == mode.themeId })
        }
    }

    @Test fun legacyBuiltInsKeepDataButRenderAsAuriaFlow() {
        assertEquals(AuriaVisualMode.Flow, AuriaVisualMode.fromTheme(LevyraThemes.LEVYRA_AURA))
        assertNull(AuriaVisualMode.fromTheme("custom_unknown"))
        assertEquals(AuriaVisualMode.Flow, AuriaVisualMode.fromTheme(LevyraThemes.AURIA_HALO))
        assertEquals(LevyraThemes.AURIA_FLOW, LevyraThemes.byId("").id)
        assertEquals(LevyraThemes.AURIA_HALO, LevyraThemes.byId(LevyraThemes.AURIA_HALO).id)
    }
}
