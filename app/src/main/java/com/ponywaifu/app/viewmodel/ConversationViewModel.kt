
package com.ponywaifu.app.viewmodel

import androidx.lifecycle.ViewModel
import com.ponywaifu.app.model.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConversationViewModel : ViewModel() {

    private val _character = MutableStateFlow<Character?>(null)
    val character = _character.asStateFlow()

    private val _subtitle = MutableStateFlow<String?>(null)
    val subtitle = _subtitle.asStateFlow()

    private val _isResponding = MutableStateFlow(false)
    val isResponding = _isResponding.asStateFlow()

    // Current scene mood, whether automatically or manually selected.
    private val _currentMood = MutableStateFlow("neutral")
    val currentMood = _currentMood.asStateFlow()

    private val _history =
        MutableStateFlow<List<ConversationTurn>>(emptyList())
    val history = _history.asStateFlow()

    /**
     * Select the character for the active conversation.
     *
     * For now, a different character starts a new conversation.
     * Selecting the same character again preserves its state.
     */
    fun selectCharacter(character: Character) {
        if (_character.value?.id == character.id) {
            _character.value = character
            return
        }

        _character.value = character
        _subtitle.value = null
        _isResponding.value = false
        _currentMood.value = "neutral"
        _history.value = emptyList()
    }

    /**
     * Manually override the current scene mood.
     *
     * Only call this when manual mood selection is enabled.
     * The UI will supply values from the server-provided mood list.
     */
    fun selectMood(mood: String, availableMoods: List<String>) {
        if (mood !in availableMoods) {
            return
        }

        _currentMood.value = mood
    }

    /**
     * Update the current mood following a completed server response.
     */
    fun updateMood(mood: String) {
        _currentMood.value = mood
    }

    /**
     * Start submitting a message to the conversation pipeline.
     *
     * Networking will be implemented in the next stage.
     */
    fun sendMessage(message: String) {
        val text = message.trim()

        if (text.isEmpty() || _isResponding.value) {
            return
        }

        if (_character.value == null) {
            return
        }

        _isResponding.value = true
        _subtitle.value = null

        // Future llm.chat request:
        // - selected character ID
        // - text
        // - _currentMood.value
        // - _history.value
    }

    /**
     * Subtitles are updated when their associated audio
     * segment starts playback, not when it is received.
     */
    fun updateSubtitle(text: String?) {
        _subtitle.value = text
    }

    /**
     * Called when the complete response and playback finish.
     */
    fun finishResponse() {
        _isResponding.value = false
        _subtitle.value = null
    }
}

/**
 * Temporary conversation history model.
 *
 * We'll replace this with the backend's structured
 * user/assistant message representation when integrating llm.chat.
 */
data class ConversationTurn(
    val role: String,
    val content: String
)
