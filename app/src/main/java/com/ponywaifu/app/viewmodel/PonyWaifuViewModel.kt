
package com.ponywaifu.app.viewmodel

import androidx.lifecycle.viewModelScope
import com.ponywaifu.app.network.ServerConnectionTester
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.ponywaifu.app.data.CharacterRepository
import com.ponywaifu.app.model.Character

class PonyWaifuViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val connectionTester = ServerConnectionTester()

    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters = _characters.asStateFlow()

    private var characterRepository: CharacterRepository? = null

    private val _isTesting = MutableStateFlow(false)
    val isTesting = _isTesting.asStateFlow()

    private val _connectionSuccessful = MutableStateFlow(false)
    val connectionSuccessful = _connectionSuccessful.asStateFlow()

    private val _connectionMessage = MutableStateFlow<String?>(null)
    val connectionMessage = _connectionMessage.asStateFlow()

    private var testedAddress: String? = null
    private var testGeneration = 0

    fun testServerConnection(address: String) {
        val generation = ++testGeneration

        _isTesting.value = true
        _connectionSuccessful.value = false
        _connectionMessage.value = "Connecting to server..."
        testedAddress = null

        connectionTester.testConnection(
            serverAddress = address,

            onSuccess = {
                viewModelScope.launch {
                    if (generation == testGeneration) {
                        _isTesting.value = false
                        _connectionSuccessful.value = true
                        _connectionMessage.value = "Connection successful!"
                        testedAddress = address
                    }
                }
            },

            onError = { error ->
                viewModelScope.launch {
                    if (generation == testGeneration) {
                        _isTesting.value = false
                        _connectionSuccessful.value = false
                        _connectionMessage.value = error
                        testedAddress = null
                    }
                }
            }
        )
    }

    fun resetConnectionTest() {
        testGeneration++
        _isTesting.value = false
        _connectionSuccessful.value = false
        _connectionMessage.value = null
        testedAddress = null
    }

    fun isAddressVerified(address: String): Boolean {
        return _connectionSuccessful.value &&
                testedAddress == address
    }

    fun loadCharacters(address: String) {
        characterRepository?.close()

        val repository = CharacterRepository(
            context = getApplication(),
            serverUrl = "ws://$address/v1/ws"
        )

        characterRepository = repository
        _characters.value = emptyList()

        repository.loadCached { cached ->
            if (characterRepository === repository) {
                _characters.value = cached
            }
        }

        repository.refresh(
            onUpdated = { updated ->
                if (characterRepository === repository) {
                    _characters.value = updated

                    Log.i(
                        "PonyWaifu",
                        "Loaded ${updated.size} characters"
                    )
                }
            },

            onError = { error ->
                if (characterRepository === repository) {
                    Log.e(
                        "PonyWaifu",
                        "Character refresh failed: $error"
                    )
                }
            }
        )
    }

    override fun onCleared() {
        characterRepository?.close()
        super.onCleared()
    }
}
