
package com.ponywaifu.app.data

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray

class DebugSettingsRepository(
    context: Context
) {
    private val preferences = context.applicationContext
        .getSharedPreferences(
            "pony_waifu_debug_settings",
            Context.MODE_PRIVATE
        )

    var manualMoodEnabled: Boolean
        get() = preferences.getBoolean(
            "manual_mood_enabled",
            false
        )
        set(value) {
            preferences.edit {
                putBoolean("manual_mood_enabled", value)
            }
        }

    var typingEnabled: Boolean
        get() = preferences.getBoolean(
            "typing_enabled",
            true
        )
        set(value) {
            preferences.edit {
                putBoolean("typing_enabled", value)
            }
        }

    fun getCachedMoods(serverAddress: String): List<String> {
        val json = preferences.getString(
            serverKey(serverAddress, "available_moods"),
            null
        ) ?: return emptyList()

        return try {
            val array = JSONArray(json)

            List(array.length()) { index ->
                array.getString(index)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun setCachedMoods(
        serverAddress: String,
        moods: List<String>
    ) {
        val array = JSONArray()

        moods.forEach { mood ->
            array.put(mood)
        }

        preferences.edit {
            putString(
                serverKey(serverAddress, "available_moods"),
                array.toString()
            )
        }
    }

    private fun serverKey(
        serverAddress: String,
        setting: String
    ): String {
        return "${serverAddress.trim().lowercase()}::$setting"
    }
}
