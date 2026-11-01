package com.luc4n3x.levyra.desktop.app.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luc4n3x.levyra.desktop.app.state.Destination
import com.luc4n3x.levyra.desktop.app.ui.components.AuriaHaloMark
import com.luc4n3x.levyra.desktop.app.ui.i18n.LocalStrings

/**
 * AURIA V4 desktop navigation: an editorial command index, deliberately
 * not Levyra's vertical pill sidebar. Rest of desktop routes stay untouched.
 */
@Composable
internal fun AuriaDesktopCommandRail(
    destination: Destination,
    hasActiveTrack: Boolean,
    isPlaying: Boolean,
    onNavigate: (Destination) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val strings = LocalStrings.current
    val items = listOf(
        Triple(Destination.HOME, strings.navHome, "01"),
        Triple(Destination.SEARCH, strings.navSearch, "02"),
        Triple(Destination.DISCOVER, strings.navDiscover, "03"),
        Triple(Destination.LIBRARY, strings.navLibrary, "04"),
        Triple(Destination.LOCAL_MUSIC, strings.localMusic, "05"),
        Triple(Destination.NOW_PLAYING, strings.navNowPlaying, "06")
    )
    Column(
        modifier = Modifier.width(218.dp).fillMaxHeight()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 38.dp, bottomStart = 26.dp, bottomEnd = 8.dp))
            .background(
                Brush.verticalGradient(
                    listOf(colors.surfaceContainer, colors.surfaceContainerLow, colors.surface)
                )
            )
            .border(
                width = 1.dp,
                color = colors.outlineVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 38.dp, bottomStart = 26.dp, bottomEnd = 8.dp)
            )
            .padding(horizontal = 15.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AuriaHaloMark(modifier = Modifier.size(41.dp), primary = colors.primary)
            Column {
                Text("AURIA", color = colors.onSurface, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Text("SOUND / SYSTEM", color = colors.primary, fontSize = 10.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
            }
        }
        Spacer(Modifier.height(27.dp))
        Text("DIRECTORIO", color = colors.onSurfaceVariant,
            fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        items.forEach { (route, label, index) ->
            val active = destination == route ||
                (route == Destination.SEARCH && destination == Destination.COLLECTION) ||
                (route == Destination.LIBRARY && destination == Destination.PLAYLIST)
            Row(
                modifier = Modifier.fillMaxWidth().height(if (active) 62.dp else 50.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                    .background(if (active) colors.primaryContainer else colors.surface.copy(alpha = 0f))
                    .clickable(onClick = { onNavigate(route) }).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Text(index, color = if (active) colors.primary else colors.onSurfaceVariant,
                    fontSize = 11.sp, fontWeight = FontWeight.Black)
                Text(label, color = colors.onSurface, fontSize = if (active) 15.sp else 13.sp,
                    modifier = Modifier.weight(1f),
                    fontWeight = if (active) FontWeight.Black else FontWeight.Medium)
                if (active) Box(Modifier.size(7.dp).background(colors.primary, RoundedCornerShape(2.dp)))
            }
        }
        Spacer(Modifier.weight(1f))
        Column(
            Modifier.fillMaxWidth().background(colors.surfaceContainerHigh, RoundedCornerShape(9.dp))
                .padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("AURIA / SIGNAL", color = colors.primary, fontSize = 10.sp,
                fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Text(if (hasActiveTrack && isPlaying) "REPRODUCIENDO" else if (hasActiveTrack) "EN PAUSA" else "SIN REPRODUCCIÓN",
                color = colors.onSurfaceVariant, fontSize = 11.sp)
        }
        Row(
            Modifier.fillMaxWidth().height(48.dp)
                .clickable(onClick = { onNavigate(Destination.SETTINGS) })
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("07", color = colors.primary, fontWeight = FontWeight.Black, fontSize = 11.sp)
            Spacer(Modifier.width(13.dp))
            Text(strings.navSettings, color = colors.onSurface, fontWeight = FontWeight.Bold,
                fontSize = 13.sp)
        }
    }
}
