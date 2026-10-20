
package com.ponywaifu.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ponywaifu.app.data.ServerSettings
import com.ponywaifu.app.viewmodel.PonyWaifuViewModel

private enum class Screen {
    SERVER_CONFIGURATION,
    CHARACTER_SELECTION,
    SETTINGS,
    DEBUG_SETTINGS
}

@Composable
fun PonyWaifuApp(
    viewModel: PonyWaifuViewModel,
    serverSettings: ServerSettings
) {
    var serverAddress by rememberSaveable {
        mutableStateOf(serverSettings.serverAddress)
    }

    var currentScreen by rememberSaveable {
        mutableStateOf(
            if (serverAddress == null) {
                Screen.SERVER_CONFIGURATION
            } else {
                Screen.CHARACTER_SELECTION
            }
        )
    }

    val isTesting by viewModel.isTesting.collectAsStateWithLifecycle()
    val connectionSuccessful by viewModel.connectionSuccessful.collectAsStateWithLifecycle()
    val connectionMessage by viewModel.connectionMessage.collectAsStateWithLifecycle()
    val characters by viewModel.characters.collectAsStateWithLifecycle()

    LaunchedEffect(serverAddress) {
        serverAddress?.let { address ->
            viewModel.loadCharacters(address)
        }
    }

    fun navigateBack() {
        when (currentScreen) {
            Screen.DEBUG_SETTINGS -> {
                currentScreen = Screen.SETTINGS
            }

            Screen.SETTINGS -> {
                currentScreen = Screen.CHARACTER_SELECTION
            }

            Screen.SERVER_CONFIGURATION -> {
                if (serverAddress != null) {
                    viewModel.resetConnectionTest()
                    currentScreen = Screen.SETTINGS
                }
            }

            Screen.CHARACTER_SELECTION -> Unit
        }
    }

    BackHandler(
        enabled = currentScreen == Screen.SETTINGS ||
                currentScreen == Screen.DEBUG_SETTINGS ||
                (currentScreen == Screen.SERVER_CONFIGURATION &&
                        serverAddress != null)
    ) {
        navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.SERVER_CONFIGURATION -> {
                    key(currentScreen) {
                        ServerConfigurationScreen(
                            initialAddress = serverAddress.orEmpty(),
                            isTesting = isTesting,
                            connectionSuccessful = connectionSuccessful,
                            connectionMessage = connectionMessage,
                            onAddressChanged = {
                                viewModel.resetConnectionTest()
                            },
                            onTestConnection = { address ->
                                viewModel.testServerConnection(address)
                            },
                            onConnect = { address ->
                                if (viewModel.isAddressVerified(address)) {
                                    serverSettings.serverAddress = address
                                    serverAddress = address
                                    currentScreen = Screen.CHARACTER_SELECTION
                                }
                            }
                        )
                    }
                }

                Screen.CHARACTER_SELECTION -> {
                    CharacterSelectionScreen(
                        characters = characters,
                        onCharacterSelected = {
                            // Conversation screen comes later.
                        },
                        onSettingsClick = {
                            currentScreen = Screen.SETTINGS
                        }
                    )
                }

                Screen.SETTINGS -> {
                    SettingsScreen(
                        serverAddress = serverAddress.orEmpty(),
                        onBack = {
                            currentScreen = Screen.CHARACTER_SELECTION
                        },
                        onServerConfigurationClick = {
                            viewModel.resetConnectionTest()
                            currentScreen = Screen.SERVER_CONFIGURATION
                        },
                        onDebugSettingsClick = {
                            currentScreen = Screen.DEBUG_SETTINGS
                        }
                    )
                }

                Screen.DEBUG_SETTINGS -> {
                    Column {
                        Text("Debug Settings")

                        Button(
                            onClick = {
                                currentScreen = Screen.SETTINGS
                            }
                        ) {
                            Text("Back to Settings")
                        }
                    }
                }
            }
        }
    }
}
