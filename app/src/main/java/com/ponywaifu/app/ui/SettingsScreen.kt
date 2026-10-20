package com.ponywaifu.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Palette

@Composable
fun SettingsScreen(
    serverAddress: String,
    onBack: () -> Unit,
    onServerConfigurationClick: () -> Unit,
    onAppearanceSettingsClick: () -> Unit,
    onDebugSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )

        SettingsItem(
            title = "Appearance",
            description = "Colour palettes and display preferences",
            icon = {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null
                )
            },
            onClick = onAppearanceSettingsClick
        )

        HorizontalDivider()

        SettingsItem(
            title = "Server Configuration",
            description = serverAddress,
            icon = {
                Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = null
                )
            },
            onClick = onServerConfigurationClick
        )

        HorizontalDivider()

        SettingsItem(
            title = "Debug Settings",
            description = "Development and testing options",
            icon = {
                Icon(
                    imageVector = Icons.Default.BugReport,
                    contentDescription = null
                )
            },
            onClick = onDebugSettingsClick
        )

        HorizontalDivider()
    }
}

@Composable
private fun SettingsItem(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        icon()

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

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null
        )
    }
}
