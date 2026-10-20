
package com.ponywaifu.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ponywaifu.app.data.DebugSettingsRepository
import com.ponywaifu.app.network.SceneMoodWebSocketClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DebugSettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = DebugSettingsRepository(
        application.applicationContext
    )

    private val moodClient = SceneMoodWebSocketClient()

    // Persistent debug settings

    private val _manualMoodEnabled = MutableStateFlow(
        repository.manualMoodEnabled
    )
    val manualMoodEnabled = _manualMoodEnabled.asStateFlow()

    private val _typingEnabled = MutableStateFlow(
        repository.typingEnabled
    )
    val typingEnabled = _typingEnabled.asStateFlow()

    // Server-provided scene moods

    private val _availableMoods = MutableStateFlow<List<String>>(emptyList())
    val availableMoods = _availableMoods.asStateFlow()

    private val _isLoadingMoods = MutableStateFlow(false)
    val isLoadingMoods = _isLoadingMoods.asStateFlow()

    private val _moodError = MutableStateFlow<String?>(null)
    val moodError = _moodError.asStateFlow()

    private var currentServerAddress: String? = null

    // Used to ignore responses from older requests.
    private var moodRequestGeneration = 0

    /**
     * Load saved settings and cached moods when the
     * configured server changes.
     */
    fun loadSettings(serverAddress: String) {
        if (currentServerAddress == serverAddress) {
            return
        }

        moodRequestGeneration++
        currentServerAddress = serverAddress

        _manualMoodEnabled.value = repository.manualMoodEnabled
        _typingEnabled.value = repository.typingEnabled
        _availableMoods.value = repository.getCachedMoods(serverAddress)
        _isLoadingMoods.value = false
        _moodError.value = null

        if (
            _manualMoodEnabled.value &&
            _availableMoods.value.isEmpty()
        ) {
            refreshMoods()
        }
    }

    /**
     * Enable or disable manual mood selection.
     */
    fun setManualMoodEnabled(enabled: Boolean) {
        repository.manualMoodEnabled = enabled
        _manualMoodEnabled.value = enabled

        if (!enabled) {
            moodRequestGeneration++
            _isLoadingMoods.value = false
            _moodError.value = null
            return
        }

        if (_availableMoods.value.isEmpty()) {
            refreshMoods()
        }
    }

    /**
     * Enable or disable typed message input.
     */
    fun setTypingEnabled(enabled: Boolean) {
        repository.typingEnabled = enabled
        _typingEnabled.value = enabled
    }

    /**
     * Retrieve the current server's available moods,
     * replacing the cached list on success.
     */
    fun refreshMoods() {
        val address = currentServerAddress ?: return

        if (_isLoadingMoods.value) {
            return
        }

        val generation = ++moodRequestGeneration

        _isLoadingMoods.value = true
        _moodError.value = null

        moodClient.getAllMoods(
            serverAddress = address,

            onSuccess = { moods ->
                viewModelScope.launch(Dispatchers.Main) {
                    if (
                        generation != moodRequestGeneration ||
                        address != currentServerAddress
                    ) {
                        return@launch
                    }

                    repository.setCachedMoods(address, moods)

                    _availableMoods.value = moods
                    _isLoadingMoods.value = false
                    _moodError.value = null
                }
            },

            onError = { error ->
                viewModelScope.launch(Dispatchers.Main) {
                    if (
                        generation != moodRequestGeneration ||
                        address != currentServerAddress
                    ) {
                        return@launch
                    }

                    _isLoadingMoods.value = false
                    _moodError.value = error

                    // Keep existing cached moods if refresh fails.
                }
            }
        )
    }

    override fun onCleared() {
        moodRequestGeneration++
        moodClient.close()
        super.onCleared()
    }
}
