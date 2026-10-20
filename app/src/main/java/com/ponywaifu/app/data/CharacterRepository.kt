
package com.ponywaifu.app.data

import android.content.Context
import com.ponywaifu.app.model.Character
import com.ponywaifu.app.network.CastWebSocketClient
import java.util.concurrent.Executors

class CharacterRepository(
    context: Context,
    serverUrl: String
) {
    private val cache = CharacterCache(
        context.applicationContext
    )

    private val network = CastWebSocketClient(serverUrl)

    private val executor = Executors.newSingleThreadExecutor()

    /**
     * Load previously cached characters without connecting
     * to the server.
     */
    fun loadCached(
        onLoaded: (List<Character>) -> Unit
    ) {
        executor.execute {
            onLoaded(cache.load())
        }
    }

    /**
     * Request the current cast from the server and
     * synchronise it with the local cache.
     */
    fun refresh(
        onUpdated: (List<Character>) -> Unit,
        onError: (String) -> Unit
    ) {
        network.fetchCharacters(
            onSuccess = { remote ->
                executor.execute {
                    try {
                        val characters = cache.synchronise(remote)
                        onUpdated(characters)
                    } catch (e: Exception) {
                        onError(
                            "Unable to cache characters: ${e.message}"
                        )
                    }
                }
            },
            onError = onError
        )
    }

    /**
     * Release the repository's background executor.
     */
    fun close() {
        executor.shutdown()
    }
}
