
package com.ponywaifu.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ponywaifu.app.data.ServerSettings
import com.ponywaifu.app.model.Character
import com.ponywaifu.app.viewmodel.AppearanceSettingsViewModel
import com.ponywaifu.app.viewmodel.DebugSettingsViewModel
import com.ponywaifu.app.viewmodel.PonyWaifuViewModel
import com.ponywaifu.app.viewmodel.ConversationViewModel

private enum class Screen {
    SERVER_CONFIGURATION,
    CHARACTER_SELECTION,
    CONVERSATION,
    SETTINGS,
    APPEARANCE_SETTINGS,
    DEBUG_SETTINGS
}

@Composable
fun PonyWaifuNavigation(
    viewModel: PonyWaifuViewModel,
    appearanceViewModel: AppearanceSettingsViewModel,
    debugSettingsViewModel: DebugSettingsViewModel,
    conversationViewModel: ConversationViewModel,
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

    val currentMood by conversationViewModel.currentMood
        .collectAsStateWithLifecycle()

    val subtitle by conversationViewModel.subtitle
        .collectAsStateWithLifecycle()

    val isResponding by conversationViewModel.isResponding
        .collectAsStateWithLifecycle()

    var selectedCharacter by remember {
        mutableStateOf<Character?>(null)
    }

    // Application state
    val isTesting by viewModel.isTesting.collectAsStateWithLifecycle()
    val connectionSuccessful by viewModel.connectionSuccessful.collectAsStateWithLifecycle()
    val connectionMessage by viewModel.connectionMessage.collectAsStateWithLifecycle()
    val characters by viewModel.characters.collectAsStateWithLifecycle()

    // Appearance settings state
    val selectedPalette by appearanceViewModel.selectedPalette
        .collectAsStateWithLifecycle()

    // Debug settings state
    val manualMoodEnabled by debugSettingsViewModel.manualMoodEnabled
        .collectAsStateWithLifecycle()

    val availableMoods by debugSettingsViewModel.availableMoods
        .collectAsStateWithLifecycle()

    val typingEnabled by debugSettingsViewModel.typingEnabled
        .collectAsStateWithLifecycle()

    val isLoadingMoods by debugSettingsViewModel.isLoadingMoods
        .collectAsStateWithLifecycle()

    val moodError by debugSettingsViewModel.moodError
        .collectAsStateWithLifecycle()

    // Load characters and server-specific debug settings
    // whenever the configured server changes.
    LaunchedEffect(serverAddress) {
        serverAddress?.let { address ->
            viewModel.loadCharacters(address)
            debugSettingsViewModel.loadSettings(address)
        }
    }

    fun navigateBack() {
        when (currentScreen) {
            Screen.CONVERSATION -> {
                currentScreen = Screen.CHARACTER_SELECTION
            }

            Screen.DEBUG_SETTINGS,
            Screen.APPEARANCE_SETTINGS -> {
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
        enabled = currentScreen == Screen.CONVERSATION ||
                currentScreen == Screen.SETTINGS ||
                currentScreen == Screen.DEBUG_SETTINGS ||
                currentScreen == Screen.APPEARANCE_SETTINGS ||
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
                        onCharacterSelected = { character ->
                            conversationViewModel.selectCharacter(character)
                            selectedCharacter = character
                            currentScreen = Screen.CONVERSATION
                        },
                        onSettingsClick = {
                            currentScreen = Screen.SETTINGS
                        }
                    )
                }


                Screen.CONVERSATION -> {
                    selectedCharacter?.let { character ->
                        ConversationScreen(
                            character = character,
                            subtitle = subtitle,
                            isResponding = isResponding,
                            typingEnabled = typingEnabled,
                            manualMoodEnabled = manualMoodEnabled,
                            availableMoods = availableMoods,
                            selectedMood = currentMood,
                            onMoodSelected = { mood ->
                                conversationViewModel.selectMood(
                                    mood,
                                    availableMoods
                                )
                            },
                            onBack = {
                                currentScreen = Screen.CHARACTER_SELECTION
                            },
                            onSendMessage = {
                                // We'll connect this when we implement llm.chat.
                                // Do not call sendMessage() yet because
                                // networking isn't implemented.
                            }
                        )
                    }
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
                        onAppearanceSettingsClick = {
                            currentScreen = Screen.APPEARANCE_SETTINGS
                        },
                        onDebugSettingsClick = {
                            currentScreen = Screen.DEBUG_SETTINGS
                        }
                    )
                }

                Screen.APPEARANCE_SETTINGS -> {
                    AppearanceSettingsScreen(
                        selectedPalette = selectedPalette,
                        onPaletteSelected = { palette ->
                            appearanceViewModel.selectPalette(palette)
                        },
                        onBack = {
                            currentScreen = Screen.SETTINGS
                        }
                    )
                }

                Screen.DEBUG_SETTINGS -> {
                    DebugSettingsScreen(
                        manualMoodEnabled = manualMoodEnabled,
                        typingEnabled = typingEnabled,
                        onManualMoodEnabledChanged = { enabled ->
                            debugSettingsViewModel.setManualMoodEnabled(enabled)
                        },
                        onTypingEnabledChanged = { enabled ->
                            debugSettingsViewModel.setTypingEnabled(enabled)
                        },
                        onBack = {
                            currentScreen = Screen.SETTINGS
                        }
                    )
                }
            }
        }
    }
}
