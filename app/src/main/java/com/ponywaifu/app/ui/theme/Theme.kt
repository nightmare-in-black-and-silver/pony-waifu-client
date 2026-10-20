
package com.ponywaifu.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun PonyWaifuTheme(
    palette: PonyWaifuPalette = PonyWaifuPalette.MIDNIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (palette) {
        PonyWaifuPalette.MIDNIGHT -> MidnightColorScheme
        PonyWaifuPalette.TWILIGHT_ROSE -> TwilightRoseColorScheme
        PonyWaifuPalette.DAYBREAK -> DaybreakColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
