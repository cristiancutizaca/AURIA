package com.luc4n3x.levyra.ui

import com.luc4n3x.levyra.domain.HomeStyle
import com.luc4n3x.levyra.domain.MiniPlayerStyle
import com.luc4n3x.levyra.domain.NavStyle
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode
import org.junit.Assert.assertEquals
import org.junit.Test

class AuriaChromeModesTest {
    @Test fun eachModeOwnsItsChrome() {
        assertEquals(MiniPlayerStyle.UltraCompact,
            auriaMiniStyle(AuriaVisualMode.Pulse, MiniPlayerStyle.ArtworkFocus))
        assertEquals(NavStyle.Minimal,
            auriaNavStyle(AuriaVisualMode.Studio, NavStyle.SplitDock))
        assertEquals(MiniPlayerStyle.Floating,
            auriaMiniStyle(AuriaVisualMode.Flow, MiniPlayerStyle.Connected))
        assertEquals(NavStyle.FloatingDock,
            auriaNavStyle(AuriaVisualMode.Nebula, NavStyle.SplitDock))
    }

    @Test fun fourAuriaHomeLayoutsAreDistinct() {
        assertEquals(HomeStyle.Flow, auriaHomeStyle(AuriaVisualMode.Flow, HomeStyle.Original))
        assertEquals(HomeStyle.Immersive, auriaHomeStyle(AuriaVisualMode.Nebula, HomeStyle.Original))
        assertEquals(HomeStyle.Minimal, auriaHomeStyle(AuriaVisualMode.Pulse, HomeStyle.Original))
        assertEquals(HomeStyle.Editorial, auriaHomeStyle(AuriaVisualMode.Studio, HomeStyle.Original))
    }

    @Test fun olderThemesPreserveStoredChoices() {
        assertEquals(MiniPlayerStyle.ArtworkFocus, auriaMiniStyle(null, MiniPlayerStyle.ArtworkFocus))
        assertEquals(NavStyle.ConnectedDock, auriaNavStyle(null, NavStyle.ConnectedDock))
        assertEquals(HomeStyle.Original, auriaHomeStyle(null, HomeStyle.Original))
    }
}
