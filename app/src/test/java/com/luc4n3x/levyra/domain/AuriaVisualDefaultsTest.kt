package com.luc4n3x.levyra.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class AuriaVisualDefaultsTest {
    @Test
    fun newProfilesStartWithHaloPlayerWithoutRemovingOtherModes() {
        assertEquals(PlayerVisualMode.Halo, LevyraInterfaceSettings().playerVisualMode)
        PlayerVisualMode.entries.forEach { mode ->
            assertEquals(mode, PlayerVisualMode.from(mode.name))
        }
    }
}
