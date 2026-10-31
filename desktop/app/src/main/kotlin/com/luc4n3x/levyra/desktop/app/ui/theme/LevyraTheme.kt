package com.luc4n3x.levyra.desktop.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luc4n3x.levyra.desktop.core.model.ThemeMode

private val LevyraCyan = Color(0xFFB09AFF)
private val LevyraViolet = Color(0xFFC8B4FF)
private val LevyraDeepBlue = Color(0xFF0A0F26)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB09AFF),
    onPrimary = Color(0xFF100B22),
    primaryContainer = Color(0xFF39245C),
    onPrimaryContainer = Color(0xFFF1E9FF),
    secondary = Color(0xFFADB7CD),
    onSecondary = Color(0xFF0A0F26),
    secondaryContainer = Color(0xFF252A49),
    onSecondaryContainer = Color(0xFFF1F5FF),
    tertiary = Color(0xFFFF9E8A),
    onTertiary = Color(0xFF21111A),
    background = Color(0xFF0A0F26),
    onBackground = Color(0xFFF1F5FF),
    surface = Color(0xFF171D38),
    onSurface = Color(0xFFF1F5FF),
    surfaceVariant = Color(0xFF252A49),
    onSurfaceVariant = Color(0xFFADB7CD),
    surfaceContainerLowest = Color(0xFF070B1D),
    surfaceContainerLow = Color(0xFF11172E),
    surfaceContainer = Color(0xFF171D38),
    surfaceContainerHigh = Color(0xFF252A49),
    surfaceContainerHighest = Color(0xFF303657),
    outline = Color(0xFF454B70),
    outlineVariant = Color(0xFF252A49),
    error = Color(0xFFFF453A),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFF4A1620),
    onErrorContainer = Color(0xFFFFD9DE)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF6943C9),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAE2FF),
    onPrimaryContainer = Color(0xFF25114F),
    secondary = Color(0xFF675D7C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEE7F6),
    onSecondaryContainer = Color(0xFF292136),
    tertiary = Color(0xFFA94D43),
    onTertiary = Color.White,
    background = Color(0xFFFAF8FF),
    onBackground = Color(0xFF1A1630),
    surface = Color(0xFFF5F1FC),
    onSurface = Color(0xFF1A1630),
    surfaceVariant = Color(0xFFE9E3F3),
    onSurfaceVariant = Color(0xFF514A62),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF9F6FF),
    surfaceContainer = Color(0xFFF3EEFB),
    surfaceContainerHigh = Color(0xFFE9E1F5),
    surfaceContainerHighest = Color(0xFFDED4EF),
    outline = Color(0xFF827994),
    outlineVariant = Color(0xFFCFC5DE),
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val LevyraTypography = Typography(
    headlineLarge = TextStyle(
        fontSize = 40.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.28).sp,
        lineHeight = 44.sp
    ),
    headlineMedium = TextStyle(
        fontSize = 34.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.37).sp,
        lineHeight = 38.sp
    ),
    headlineSmall = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.2.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontSize = 21.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.23.sp,
        lineHeight = 25.sp
    ),
    titleMedium = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.37).sp,
        lineHeight = 21.sp
    ),
    titleSmall = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.2).sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.37).sp,
        lineHeight = 25.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.24).sp,
        lineHeight = 22.sp
    ),
    bodySmall = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.08).sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.22).sp
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.12).sp
    ),
    labelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.08).sp
    )
)

private val LevyraShapes = Shapes(
    extraSmall = RoundedCornerShape(5.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(11.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

object LevyraBrand {
    val cyan: Color get() = LevyraCyan
    val violet: Color get() = LevyraViolet
    val deepBlue: Color get() = LevyraDeepBlue
}

object LevyraMotion {
    const val HOVER_ALPHA = 0.08f
    const val SELECTED_ALPHA = 0.12f
}

val LocalAccentColor = compositionLocalOf { LevyraCyan }

@Composable
fun LevyraTheme(themeMode: ThemeMode, content: @Composable () -> Unit) {
    val dark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = LevyraTypography,
        shapes = LevyraShapes,
        content = content
    )
}
