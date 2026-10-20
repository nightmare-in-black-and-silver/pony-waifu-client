
package com.ponywaifu.app.data

import android.content.Context
import android.util.Base64
import com.ponywaifu.app.model.Character
import com.ponywaifu.app.network.RemoteCharacter
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.security.MessageDigest

class CharacterCache(context: Context) {

    private val directory = File(
        context.filesDir,
        "characters"
    ).apply { mkdirs() }

    private val catalogue = File(directory, "catalogue.json")

    fun load(): List<Character> {
        if (!catalogue.exists()) return emptyList()

        return try {
            val array = JSONArray(catalogue.readText())

            (0 until array.length()).map { index ->
                val item = array.getJSONObject(index)

                Character(
                    id = item.getString("id"),
                    name = item.getString("name"),
                    summary = item.getString("summary"),
                    profilePicturePath = item.optString(
                        "profilePicturePath"
                    ).ifEmpty { null },
                    neutralExpressionPath = item.optString(
                        "neutralExpressionPath"
                    ).ifEmpty { null },
                    available = item.optBoolean("available", true)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun synchronise(
        remote: List<RemoteCharacter>
    ): List<Character> {
        val existing = load().associateBy { it.id }
        val receivedIds = remote.map { it.id }.toSet()

        val updated = remote.map { item ->
            val previous = existing[item.id]

            Character(
                id = item.id,
                name = item.name,
                summary = item.summary,
                profilePicturePath = saveImage(
                    item.id,
                    "profile",
                    item.profilePicture,
                    previous?.profilePicturePath
                ),
                neutralExpressionPath = saveImage(
                    item.id,
                    "neutral",
                    item.neutralExpression,
                    previous?.neutralExpressionPath
                ),
                available = true
            )
        }

        val unavailable = existing.values
            .filter { it.id !in receivedIds }
            .map { it.copy(available = false) }

        val result = updated + unavailable

        saveCatalogue(result)

        return result
    }

    private fun saveImage(
        id: String,
        kind: String,
        base64: String,
        previousPath: String?
    ): String? {
        if (base64.isEmpty()) return previousPath

        return try {
            val bytes = Base64.decode(
                base64,
                Base64.DEFAULT
            )

            val hash = sha256(bytes)
            val filename = "${sha256(id.toByteArray())}_${kind}_$hash.png"
            val file = File(directory, filename)

            if (!file.exists()) {
                file.writeBytes(bytes)
            }

            file.absolutePath
        } catch (e: Exception) {
            previousPath
        }
    }

    private fun sha256(bytes: ByteArray): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { "%02x".format(it) }
    }

    private fun saveCatalogue(
        characters: List<Character>
    ) {
        val array = JSONArray()

        characters.forEach { character ->
            array.put(JSONObject().apply {
                put("id", character.id)
                put("name", character.name)
                put("summary", character.summary)
                put("profilePicturePath", character.profilePicturePath)
                put("neutralExpressionPath", character.neutralExpressionPath)
                put("available", character.available)
            })
        }

        val temporary = File(directory, "catalogue.tmp")
        temporary.writeText(array.toString())

        if (!temporary.renameTo(catalogue)) {
            temporary.delete()
            throw IOException("Unable to save character catalogue")
        }
    }
}
