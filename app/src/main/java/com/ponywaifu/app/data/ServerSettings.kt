
package com.ponywaifu.app.data

import android.content.Context
import androidx.core.content.edit

class ServerSettings(context: Context) {

    private val preferences = context.getSharedPreferences(
        "pony_waifu_settings",
        Context.MODE_PRIVATE
    )

    var serverAddress: String?
        get() = preferences.getString("server_address", null)
        set(value) {
            preferences.edit {
                putString("server_address", value)
            }
        }
}
