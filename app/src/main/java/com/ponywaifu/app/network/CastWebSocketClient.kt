
package com.ponywaifu.app.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

data class RemoteCharacter(
    val id: String,
    val name: String,
    val summary: String,
    val profilePicture: String,
    val neutralExpression: String
)

class CastWebSocketClient(
    private val url: String
) {
    private val httpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    fun fetchCharacters(
        onSuccess: (List<RemoteCharacter>) -> Unit,
        onError: (String) -> Unit
    ) {
        val requestId = UUID.randomUUID().toString()
        val request = Request.Builder().url(url).build()

        var completed = false

        val listener = object : WebSocketListener() {
            private fun fail(message: String) {
                if (!completed) {
                    completed = true
                    onError(message)
                }
            }

            override fun onOpen(
                webSocket: WebSocket,
                response: Response
            ) {
                val event = JSONObject().apply {
                    put("type", "cast.get_all")
                    put("request_id", requestId)
                    put("payload", JSONObject())
                }

                if (!webSocket.send(event.toString())) {
                    fail("Unable to send cast request")
                    webSocket.close(1000, null)
                }
            }

            override fun onMessage(
                webSocket: WebSocket,
                text: String
            ) {
                try {
                    val event = JSONObject(text)
                    val type = event.optString("type")

                    if (type == "error") {
                        if (event.optString("request_id") == requestId) {
                            val message = event
                                .optJSONObject("payload")
                                ?.optString("message")
                                ?: "Server error"
                            fail(message)
                            webSocket.close(1000, null)
                        }
                        return
                    }

                    if (type != "cast.get_all.result" ||
                        event.optString("request_id") != requestId
                    ) {
                        return
                    }

                    val characters = event
                        .getJSONObject("payload")
                        .getJSONArray("characters")

                    val result = parseCharacters(characters)

                    if (!completed) {
                        completed = true
                        onSuccess(result)
                    }

                    webSocket.close(1000, null)
                } catch (e: Exception) {
                    fail("Invalid cast response: ${e.message}")
                    webSocket.close(1000, null)
                }
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                fail(t.message ?: "WebSocket connection failed")
            }

            override fun onClosed(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                fail("Connection closed before cast response")
            }
        }

        httpClient.newWebSocket(request, listener)
    }

    private fun parseCharacters(
        array: JSONArray
    ): List<RemoteCharacter> {
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)

            RemoteCharacter(
                id = item.getString("id"),
                name = item.getString("name"),
                summary = item.getString("summary"),
                profilePicture = item.getString("profile_picture"),
                neutralExpression = item.getString("neutral_expression")
            )
        }
    }
}
