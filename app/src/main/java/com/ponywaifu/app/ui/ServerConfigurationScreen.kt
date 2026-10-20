package com.ponywaifu.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ServerConfigurationScreen(
    initialAddress: String = "",
    isTesting: Boolean,
    connectionSuccessful: Boolean,
    connectionMessage: String?,
    onAddressChanged: () -> Unit,
    onTestConnection: (String) -> Unit,
    onConnect: (String) -> Unit
) {
    var serverAddress by rememberSaveable {
        mutableStateOf(initialAddress)
    }

    val address = serverAddress.trim()

    val validAddress = address.isNotEmpty() &&
            !address.contains("://") &&
            !address.contains("/")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pony Waifu",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Server Configuration",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 24.dp
            )
        )

        OutlinedTextField(
            value = serverAddress,
            onValueChange = {
                serverAddress = it
                onAddressChanged()
            },
            label = { Text("Server address") },
            placeholder = { Text("192.168.0.100:8000") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri
            ),
            enabled = !isTesting,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Enter the server IP address or hostname and port.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedButton(
            onClick = {
                onTestConnection(address)
            },
            enabled = validAddress && !isTesting,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Test Connection")
        }

        if (isTesting) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }

        if (connectionMessage != null) {
            Text(
                text = connectionMessage,
                color = if (connectionSuccessful) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = {
                onConnect(address)
            },
            enabled = validAddress &&
                    connectionSuccessful &&
                    !isTesting,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Connect")
        }
    }
}
