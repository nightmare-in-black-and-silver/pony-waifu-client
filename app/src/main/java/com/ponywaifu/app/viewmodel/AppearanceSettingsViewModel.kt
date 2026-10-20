
package com.ponywaifu.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ponywaifu.app.data.AppearanceSettingsRepository
import com.ponywaifu.app.ui.theme.PonyWaifuPalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppearanceSettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AppearanceSettingsRepository(
        application.applicationContext
    )

    private val _selectedPalette = MutableStateFlow(
        repository.selectedPalette
    )

    val selectedPalette = _selectedPalette.asStateFlow()

    fun selectPalette(palette: PonyWaifuPalette) {
        repository.selectedPalette = palette
        _selectedPalette.value = palette
    }
}
