
package com.ponywaifu.app.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class ServerConnectionTester {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    fun testConnection(
        serverAddress: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "ws://$serverAddress/v1/ws"
        val completed = AtomicBoolean(false)

        val request = try {
            Request.Builder()
                .url(url)
                .build()
        } catch (e: Exception) {
            onError("Invalid server address: ${e.message}")
            return
        }

        val webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String
                ) {
                    try {
                        val event = JSONObject(text)

                        if (event.optString("type") == "connection.ready") {
                            if (completed.compareAndSet(false, true)) {
                                onSuccess()
                                webSocket.close(1000, "Connection test complete")
                            }
                        }
                    } catch (e: Exception) {
                        if (completed.compareAndSet(false, true)) {
                            onError("Invalid server response: ${e.message}")
                            webSocket.close(1000, null)
                        }
                    }
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    if (completed.compareAndSet(false, true)) {
                        onError(
                            "Connection failed: ${t.message ?: "Unknown error"}"
                        )
                    }
                }

                override fun onClosed(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                ) {
                    if (completed.compareAndSet(false, true)) {
                        onError("Connection closed before server confirmation")
                    }
                }
            }
        )

        // Enforce a timeout for the entire test, including waiting
        // for the server's connection.ready event.
        client.dispatcher.executorService.execute {
            try {
                Thread.sleep(10_000)

                if (completed.compareAndSet(false, true)) {
                    webSocket.cancel()
                    onError("Connection timed out after 10 seconds")
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }
}
