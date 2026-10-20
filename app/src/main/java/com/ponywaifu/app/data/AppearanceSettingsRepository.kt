
package com.ponywaifu.app.data

import android.content.Context
import androidx.core.content.edit
import com.ponywaifu.app.ui.theme.PonyWaifuPalette

class AppearanceSettingsRepository(
    context: Context
) {
    private val preferences = context.applicationContext
        .getSharedPreferences(
            "pony_waifu_appearance",
            Context.MODE_PRIVATE
        )

    var selectedPalette: PonyWaifuPalette
        get() {
            val savedPalette = preferences.getString(
                "selected_palette",
                null
            )

            return PonyWaifuPalette.entries.firstOrNull {
                it.name == savedPalette
            } ?: PonyWaifuPalette.MIDNIGHT
        }
        set(value) {
            preferences.edit {
                putString("selected_palette", value.name)
            }
        }
}
