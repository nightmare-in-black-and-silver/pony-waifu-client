
package com.ponywaifu.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ponywaifu.app.ui.theme.PonyWaifuPalette

@Composable
fun AppearanceSettingsScreen(
    selectedPalette: PonyWaifuPalette,
    onPaletteSelected: (PonyWaifuPalette) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Appearance",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Colour palette",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Choose the colours used throughout Pony Waifu.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PaletteOption(
            title = "Midnight",
            description = "Deep navy and lavender",
            backgroundColor = Color(0xFF171A35),
            accentColor = Color(0xFFADA4E5),
            selected = selectedPalette == PonyWaifuPalette.MIDNIGHT,
            onClick = { onPaletteSelected(PonyWaifuPalette.MIDNIGHT) }
        )

        PaletteOption(
            title = "Twilight Rose",
            description = "Warm mauve, rose and peach",
            backgroundColor = Color(0xFF30263B),
            accentColor = Color(0xFFE6A3C2),
            selected = selectedPalette == PonyWaifuPalette.TWILIGHT_ROSE,
            onClick = { onPaletteSelected(PonyWaifuPalette.TWILIGHT_ROSE) }
        )

        PaletteOption(
            title = "Daybreak",
            description = "Soft ivory, lavender and teal",
            backgroundColor = Color(0xFFF7F0F8),
            accentColor = Color(0xFF8A6BB1),
            selected = selectedPalette == PonyWaifuPalette.DAYBREAK,
            onClick = { onPaletteSelected(PonyWaifuPalette.DAYBREAK) }
        )
    }
}

@Composable
private fun PaletteOption(
    title: String,
    description: String,
    backgroundColor: Color,
    accentColor: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Preview of the palette.
        Surface(
            modifier = Modifier
                .padding(4.dp),
            shape = RoundedCornerShape(12.dp),
            color = backgroundColor,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = accentColor,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Aa",
                        color = backgroundColor,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
