package com.github.slavikjunior.synchronizedclipboard.core.network

import com.github.slavikjunior.synchronizedclipboard.core.network.api.NetworkSyncApi
import com.github.slavikjunior.synchronizedclipboard.core.network.model.EncryptedClipboardDto
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import kotlinx.coroutines.CoroutineExceptionHandler

@Single
internal class KtorNetworkSyncApi(
    private val httpClient: HttpClient,
    private val json: Json,
    @Named("default_dispatcher") private val defaultDispatcher: CoroutineDispatcher,
) : NetworkSyncApi {

    private val scope = CoroutineScope(
        defaultDispatcher + SupervisorJob() + CoroutineExceptionHandler { _, exception ->
            Logger.w(tag, "Unhandled exception in KtorNetworkSyncApi scope", exception)
        }
    )
    private val tag = "KtorNetworkSyncApi"

    private val _incomingItems = MutableSharedFlow<EncryptedClipboardDto>(extraBufferCapacity = 64)
    private val incomingItems = _incomingItems.asSharedFlow()

    override suspend fun observeIncomingItems(): Flow<EncryptedClipboardDto> = incomingItems

    override suspend fun sendItem(item: EncryptedClipboardDto) {
        try {
            httpClient.webSocket(
                method = HttpMethod.Get,
                host = PlatformConfig.syncHost,
                port = PlatformConfig.syncPort,
                path = "/sync",
            ) {
                val jsonStr = json.encodeToString(EncryptedClipboardDto.serializer(), item)
                send(Frame.Text(jsonStr))
            }
        } catch (e: Exception) {
            Logger.w(tag, "Failed to send item via WebSocket", e)
        }
    }

    init {
        scope.launch {
            var reconnectDelay = 1000L
            while (true) {
                try {
                    httpClient.webSocket(
                        method = HttpMethod.Get,
                        host = PlatformConfig.syncHost,
                        port = PlatformConfig.syncPort,
                        path = "/sync",
                    ) {
                        reconnectDelay = 1000L
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val dto = json.decodeFromString(
                                    EncryptedClipboardDto.serializer(),
                                    frame.readText(),
                                )
                                _incomingItems.tryEmit(dto)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Logger.w(tag, "WebSocket error, will retry in $reconnectDelay ms", e)
                } finally {
                    delay(reconnectDelay)
                    reconnectDelay = minOf(reconnectDelay * 2, 30_000L)
                }
            }
        }
    }

    fun clear() {
        scope.cancel()
    }
}
