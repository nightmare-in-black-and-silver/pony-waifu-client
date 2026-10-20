
package com.ponywaifu.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class PonyWaifuPalette {
    MIDNIGHT,
    TWILIGHT_ROSE,
    DAYBREAK
}

val MidnightColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFFADA4E5),
    onPrimary = Color(0xFF171A35),

    primaryContainer = Color(0xFF39365D),
    onPrimaryContainer = Color(0xFFEEE9FF),

    secondary = Color(0xFF9894C9),
    onSecondary = Color(0xFF171A35),

    tertiary = Color(0xFFE5B3CC),
    onTertiary = Color(0xFF30263B),

    background = Color(0xFF171A35),
    onBackground = Color(0xFFEEE9FF),

    surface = Color(0xFF222541),
    onSurface = Color(0xFFEEE9FF),

    surfaceVariant = Color(0xFF343751),
    onSurfaceVariant = Color(0xFFC7C5DB),

    outline = Color(0xFF9290AC)
)

val TwilightRoseColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFFE6A3C2),
    onPrimary = Color(0xFF30263B),

    primaryContainer = Color(0xFF593A56),
    onPrimaryContainer = Color(0xFFFFE5F0),

    secondary = Color(0xFFE6A3A0),
    onSecondary = Color(0xFF30263B),

    tertiary = Color(0xFFF0C1A7),
    onTertiary = Color(0xFF30263B),

    background = Color(0xFF30263B),
    onBackground = Color(0xFFFAE8F0),

    surface = Color(0xFF403149),
    onSurface = Color(0xFFFAE8F0),

    surfaceVariant = Color(0xFF55435B),
    onSurfaceVariant = Color(0xFFE7CADB),

    outline = Color(0xFFB69CB6)
)

val DaybreakColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF8A6BB1),
    onPrimary = Color.White,

    primaryContainer = Color(0xFFEBDDF5),
    onPrimaryContainer = Color(0xFF38254E),

    secondary = Color(0xFF558E92),
    onSecondary = Color.White,

    tertiary = Color(0xFFAC668A),
    onTertiary = Color.White,

    background = Color(0xFFF7F0F8),
    onBackground = Color(0xFF30283A),

    surface = Color(0xFFFFFAFF),
    onSurface = Color(0xFF30283A),

    surfaceVariant = Color(0xFFEDE0EF),
    onSurfaceVariant = Color(0xFF5D5264),

    outline = Color(0xFF8C8092)
)
