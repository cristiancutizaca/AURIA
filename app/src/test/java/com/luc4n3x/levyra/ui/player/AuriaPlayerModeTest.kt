package com.luc4n3x.levyra.ui.player

import com.luc4n3x.levyra.domain.PlayerVisualMode
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode
import org.junit.Assert.assertEquals
import org.junit.Test

class AuriaPlayerModeTest {
    @Test fun matchingPlayerLayoutForEachFamily() {
        assertEquals(PlayerVisualMode.Flow, auriaPlayerVisualMode(PlayerVisualMode.Halo, AuriaVisualMode.Flow))
        assertEquals(PlayerVisualMode.Pulse, auriaPlayerVisualMode(PlayerVisualMode.Halo, AuriaVisualMode.Pulse))
        assertEquals(PlayerVisualMode.Folio, auriaPlayerVisualMode(PlayerVisualMode.Halo, AuriaVisualMode.Studio))
        assertEquals(PlayerVisualMode.Halo, auriaPlayerVisualMode(PlayerVisualMode.Halo, AuriaVisualMode.Nebula))
    }
    @Test fun explicitPlayerLayoutIsPreserved() {
        assertEquals(PlayerVisualMode.Artwork,
            auriaPlayerVisualMode(PlayerVisualMode.Artwork, AuriaVisualMode.Studio))
    }
}
