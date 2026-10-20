
package com.ponywaifu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.ponywaifu.app.data.ServerSettings
import com.ponywaifu.app.ui.PonyWaifuApp
import com.ponywaifu.app.ui.theme.PonyWaifuTheme
import com.ponywaifu.app.viewmodel.PonyWaifuViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PonyWaifuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val serverSettings = ServerSettings(applicationContext)

        setContent {
            PonyWaifuTheme {
                PonyWaifuApp(
                    viewModel = viewModel,
                    serverSettings = serverSettings
                )
            }
        }
    }
}
