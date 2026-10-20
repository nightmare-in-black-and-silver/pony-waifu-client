
package com.ponywaifu.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ponywaifu.app.data.CharacterRepository
import com.ponywaifu.app.data.ServerSettings
import com.ponywaifu.app.model.Character
import com.ponywaifu.app.network.ServerConnectionTester
import com.ponywaifu.app.ui.theme.PonyWaifuTheme

class MainActivity : ComponentActivity() {

    private lateinit var serverSettings: ServerSettings
    private val connectionTester = ServerConnectionTester()

    private var serverAddress by mutableStateOf<String?>(null)

    private var isTesting by mutableStateOf(false)
    private var connectionSuccessful by mutableStateOf(false)
    private var connectionMessage by mutableStateOf<String?>(null)

    private var testedAddress: String? = null

    private var characters by mutableStateOf<List<Character>>(emptyList())

    private var characterRepository: CharacterRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        serverSettings = ServerSettings(this)
        serverAddress = serverSettings.serverAddress

        setContent {
            PonyWaifuTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    if (serverAddress == null) {
                        ServerConfigurationScreen(
                            isTesting = isTesting,
                            connectionSuccessful = connectionSuccessful,
                            connectionMessage = connectionMessage,
                            onAddressChanged = {
                                connectionSuccessful = false
                                connectionMessage = null
                                testedAddress = null
                            },
                            onTestConnection = { address ->
                                testServerConnection(address)
                            },
                            onConnect = { address ->
                                if (connectionSuccessful &&
                                    testedAddress == address
                                ) {
                                    serverSettings.serverAddress = address
                                    serverAddress = address
                                    loadCharacters(address)
                                }
                            }
                        )
                    } else {
                        CharacterSelectionScreen(
                            characters = characters,
                            onCharacterSelected = { character ->
                                // Conversation navigation comes later.
                            }
                        )
                    }
                }
            }
        }

        // If an address was saved previously, load the cast
        // immediately when the application launches.
        serverAddress?.let { address ->
            loadCharacters(address)
        }
    }

    private fun loadCharacters(address: String) {
        characterRepository?.close()

        val repository = CharacterRepository(
            context = applicationContext,
            serverUrl = "ws://$address/v1/ws"
        )

        characterRepository = repository

        repository.loadCached { cached ->
            runOnUiThread {
                if (characterRepository === repository) {
                    characters = cached
                }
            }
        }

        repository.refresh(
            onUpdated = { updated ->
                runOnUiThread {
                    if (characterRepository === repository) {
                        characters = updated
                        Log.i(
                            "PonyWaifu",
                            "Loaded ${updated.size} characters"
                        )
                    }
                }
            },
            onError = { error ->
                runOnUiThread {
                    if (characterRepository === repository) {
                        Log.e(
                            "PonyWaifu",
                            "Character refresh failed: $error"
                        )
                    }
                }
            }
        )
    }

    private fun testServerConnection(address: String) {
        isTesting = true
        connectionSuccessful = false
        connectionMessage = "Connecting to server..."
        testedAddress = null

        connectionTester.testConnection(
            serverAddress = address,
            onSuccess = {
                runOnUiThread {
                    isTesting = false
                    connectionSuccessful = true
                    connectionMessage = "Connection successful!"
                    testedAddress = address
                }
            },
            onError = { error ->
                runOnUiThread {
                    isTesting = false
                    connectionSuccessful = false
                    connectionMessage = error
                    testedAddress = null
                }
            }
        )
    }

    override fun onDestroy() {
        characterRepository?.close()
        super.onDestroy()
    }
}
