
package com.ponywaifu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.ponywaifu.app.data.ServerSettings
import com.ponywaifu.app.ui.PonyWaifuNavigation
import com.ponywaifu.app.ui.theme.PonyWaifuTheme
import com.ponywaifu.app.viewmodel.PonyWaifuViewModel
import com.ponywaifu.app.viewmodel.AppearanceSettingsViewModel
import com.ponywaifu.app.viewmodel.ConversationViewModel
import com.ponywaifu.app.viewmodel.DebugSettingsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PonyWaifuViewModel by viewModels()

    private val conversationViewModel: ConversationViewModel by viewModels()

    private val appearanceViewModel: AppearanceSettingsViewModel by viewModels()

    private val debugSettingsViewModel: DebugSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val serverSettings = ServerSettings(applicationContext)

        setContent {
            val selectedPalette by appearanceViewModel.selectedPalette
                .collectAsStateWithLifecycle()

            PonyWaifuTheme(
                palette = selectedPalette
            ) {
                PonyWaifuNavigation(
                    viewModel = viewModel,
                    appearanceViewModel = appearanceViewModel,
                    debugSettingsViewModel = debugSettingsViewModel,
                    conversationViewModel = conversationViewModel,
                    serverSettings = serverSettings
                )
            }
        }
    }
}
