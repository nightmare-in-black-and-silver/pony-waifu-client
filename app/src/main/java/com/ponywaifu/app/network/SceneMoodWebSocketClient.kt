
package com.ponywaifu.app.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class SceneMoodWebSocketClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    /**
     * Retrieve the available scene moods from the server.
     *
     * serverAddress is the configured host:port,
     * not a separate mood endpoint.
     */
    fun getAllMoods(
        serverAddress: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        val requestId = UUID.randomUUID().toString()
        val completed = AtomicBoolean(false)

        val request = Request.Builder()
            .url("ws://$serverAddress/v1/ws")
            .build()

        var webSocket: WebSocket? = null

        fun fail(message: String) {
            if (completed.compareAndSet(false, true)) {
                onError(message)
                webSocket?.cancel()
            }
        }

        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(
                    webSocket: WebSocket,
                    response: Response
                ) {
                    val message = JSONObject().apply {
                        put("type", "scene.moods.get_all")
                        put("request_id", requestId)
                        put("payload", JSONObject())
                    }

                    if (!webSocket.send(message.toString())) {
                        fail("Failed to send scene mood request.")
                    }
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String
                ) {
                    try {
                        val response = JSONObject(text)

                        if (
                            response.optString("type") !=
                            "scene.moods.get_all.result"
                        ) {
                            return
                        }

                        if (
                            response.optString("request_id") != requestId
                        ) {
                            return
                        }

                        val payload = response.getJSONObject("payload")
                        val moodsArray = payload.getJSONArray("moods")

                        val moods = List(moodsArray.length()) { index ->
                            moodsArray.getString(index)
                        }

                        if (completed.compareAndSet(false, true)) {
                            onSuccess(moods)
                            webSocket.close(1000, "Request complete")
                        }

                    } catch (exception: Exception) {
                        fail(
                            "Failed to parse scene moods: ${exception.message}"
                        )
                    }
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    fail(
                        t.message ?: "Scene mood request failed."
                    )
                }

                override fun onClosed(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                ) {
                    if (!completed.get()) {
                        fail("Connection closed before scene moods arrived.")
                    }
                }
            }
        )

        // Overall request timeout, including waiting for the response.
        client.dispatcher.executorService.execute {
            try {
                Thread.sleep(10_000)
                fail("Timed out waiting for scene moods.")
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    fun close() {
        client.dispatcher.cancelAll()
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }
}
