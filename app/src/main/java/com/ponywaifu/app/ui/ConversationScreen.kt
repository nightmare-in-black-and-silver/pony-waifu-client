
package com.ponywaifu.app.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ponywaifu.app.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton

@Composable
fun ConversationScreen(
    character: Character,
    subtitle: String?,
    isResponding: Boolean,
    typingEnabled: Boolean,
    manualMoodEnabled: Boolean,
    availableMoods: List<String>,
    selectedMood: String?,
    onMoodSelected: (String) -> Unit,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var messageInput by remember(character.id) {
        mutableStateOf("")
    }

    var moodDropdownExpanded by remember {
        mutableStateOf(false)
    }

    val bitmap by produceState<android.graphics.Bitmap?>(
        initialValue = null,
        key1 = character.neutralExpressionPath
    ) {
        value = withContext(Dispatchers.IO) {
            character.neutralExpressionPath?.let { path ->
                BitmapFactory.decodeFile(path)
            }
        }
    }

    fun sendMessage() {
        val message = messageInput.trim()

        if (message.isNotEmpty() && !isResponding) {
            onSendMessage(message)
            messageInput = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // Character artwork, scaled without stretching or cropping.
        bitmap?.let { image ->
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = character.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Character name and back navigation.
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(
                    alpha = 0.85f
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            }


            if (manualMoodEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box {
                        TextButton(
                            onClick = { moodDropdownExpanded = true },
                            enabled = availableMoods.isNotEmpty()
                        ) {
                            Text(
                                text = selectedMood ?: "Select mood"
                            )
                        }

                        DropdownMenu(
                            expanded = moodDropdownExpanded,
                            onDismissRequest = {
                                moodDropdownExpanded = false
                            }
                        ) {
                            availableMoods.forEach { mood ->
                                DropdownMenuItem(
                                    text = { Text(mood) },
                                    onClick = {
                                        onMoodSelected(mood)
                                        moodDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }


            // Keep the character artwork visible.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            // Only show subtitles for the currently playing audio.
            if (!subtitle.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.82f
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Text input for testing conversations.
            if (typingEnabled) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.94f
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text("Type a message...")
                            },
                            maxLines = 4,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = { sendMessage() }
                            )
                        )

                        IconButton(
                            onClick = { sendMessage() },
                            enabled = messageInput.isNotBlank() && !isResponding
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send message"
                            )
                        }
                    }
                }
            }
        }
    }
}
