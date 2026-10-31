package com.luc4n3x.levyra.ui

import com.luc4n3x.levyra.domain.MiniPlayerStyle
import com.luc4n3x.levyra.domain.NavStyle
import com.luc4n3x.levyra.ui.theme.AuriaVisualMode
import org.junit.Assert.assertEquals
import org.junit.Test

class AuriaChromeModesTest {
    @Test fun modesUseCompactIndependentChrome() {
        assertEquals(MiniPlayerStyle.UltraCompact,
            auriaMiniStyle(AuriaVisualMode.Pulse, MiniPlayerStyle.Connected))
        assertEquals(NavStyle.Minimal,
            auriaNavStyle(AuriaVisualMode.Studio, NavStyle.ConnectedDock))
        assertEquals(MiniPlayerStyle.Floating,
            auriaMiniStyle(AuriaVisualMode.Flow, MiniPlayerStyle.Connected))
    }

    @Test fun customChoicesAndOlderThemesArePreserved() {
        assertEquals(MiniPlayerStyle.ArtworkFocus,
            auriaMiniStyle(AuriaVisualMode.Flow, MiniPlayerStyle.ArtworkFocus))
        assertEquals(NavStyle.SplitDock,
            auriaNavStyle(AuriaVisualMode.Pulse, NavStyle.SplitDock))
        assertEquals(NavStyle.ConnectedDock,
            auriaNavStyle(null, NavStyle.ConnectedDock))
    }
}
