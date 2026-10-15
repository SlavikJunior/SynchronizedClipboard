package com.github.slavikjunior.synchronizedclipboard.core.network

import com.github.slavikjunior.synchronizedclipboard.core.network.api.NetworkSyncApi
import com.github.slavikjunior.synchronizedclipboard.core.network.model.ClipboardItemResponse
import com.github.slavikjunior.synchronizedclipboard.core.network.model.ClipboardItemsListResponse
import com.github.slavikjunior.synchronizedclipboard.core.network.model.ClipboardType
import com.github.slavikjunior.synchronizedclipboard.core.network.model.CreateClipboardItemRequest
import com.github.slavikjunior.synchronizedclipboard.core.network.model.EncryptedClipboardDto
import com.github.slavikjunior.synchronizedclipboard.core.network.model.RegisterDeviceRequest
import com.github.slavikjunior.synchronizedclipboard.core.network.model.RegisterDeviceResponse
import com.github.slavikjunior.synchronizedclipboard.core.network.model.WebSocketMessage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

/**
 * Реализация [NetworkSyncApi] поверх Ktor (KMP).
 *
 * Топология трафика (совпадает с бэкендом):
 * - отправка — только REST `POST /api/v1/clipboard`;
 * - приём — WS `/ws/clipboard?deviceId=`, сервер шлёт `NEW_ITEM`
 *   с метаданными, payload докачивается `GET /api/v1/clipboard?page`.
 *
 * Токен авторизации пред-фетчится до построения запроса: builder-лямбды
 * Ktor синхронные, suspend-вызов [AuthTokenProvider.validToken] внутри
 * них невозможен.
 *
 * Ретраи: WS-реконнект с backoff 1с → 30с; REST-401 — один refresh токена
 * и повтор. Ошибки не всплывают исключениями — логи + [Result.failure],
 * reconnect-цикл самовосстанавливается.
 */
@Single
internal class KtorNetworkSyncApi(
    private val httpClient: HttpClient,
    private val json: Json,
    private val syncConfigProvider: SyncConfigProvider,
    private val tokenProvider: AuthTokenProvider,
    private val deviceIdentityProvider: DeviceIdentityProvider,
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

    // Гвард однократного запуска listen-цикла. Mutex вместо JVM-only
    // synchronized/@Volatile (commonMain компилируется и для iOS).
    private val listenMutex = Mutex()
    private var isListening = false

    override suspend fun registerDeviceIfNeeded(): Result<String> {
        deviceIdentityProvider.currentDeviceId()?.let { return Result.success(it) }

        return try {
            val token = tokenProvider.validToken()
            val config = syncConfigProvider.current()
            val response = httpClient.post {
                restUrl(config, PATH_DEVICES)
                header(HttpHeaders.Authorization, bearer(token))
                contentType(ContentType.Application.Json)
                setBody(RegisterDeviceRequest(deviceName()))
            }

            if (!response.status.isSuccess()) {
                return Result.failure(
                    IllegalStateException("Device registration failed: ${response.status} ${response.bodyAsText()}"),
                )
            }

            val registered = json.decodeFromString(RegisterDeviceResponse.serializer(), response.bodyAsText())
            deviceIdentityProvider.saveDeviceId(registered.id)
            Result.success(registered.id)
        } catch (e: Exception) {
            Logger.w(tag, "Device registration error", e)
            Result.failure(e)
        }
    }

    override suspend fun sendItem(payload: String, type: ClipboardType): Result<Unit> {
        // Самолечение: если устройство ещё не зарегистрировано — регистрируем
        // здесь же, чтобы «первая отправка» не терялась.
        val deviceId = registerDeviceIfNeeded().getOrElse { return Result.failure(it) }

        val requestBody = CreateClipboardItemRequest(deviceId = deviceId, type = type, payload = payload)

        return try {
            val config = syncConfigProvider.current()
            val firstAttemptToken = tokenProvider.validToken()
            val response = httpClient.post {
                restUrl(config, PATH_CLIPBOARD)
                header(HttpHeaders.Authorization, bearer(firstAttemptToken))
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status == HttpStatusCode.Unauthorized) {
                // Токен протух: refresh + ровно один повтор.
                tokenProvider.invalidate()
                val retryToken = tokenProvider.validToken()
                val retry = httpClient.post {
                    restUrl(config, PATH_CLIPBOARD)
                    header(HttpHeaders.Authorization, bearer(retryToken))
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }
                if (retry.status.isSuccess()) {
                    return Result.success(Unit)
                }
                return Result.failure(IllegalStateException("Send failed after token refresh: ${retry.status}"))
            }

            if (!response.status.isSuccess()) {
                return Result.failure(
                    IllegalStateException("Send failed: ${response.status} ${response.bodyAsText()}"),
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Logger.w(tag, "Send item error", e)
            Result.failure(e)
        }
    }

    override fun observeIncomingItems(): Flow<EncryptedClipboardDto> {
        startNetworkListening()
        return incomingItems
    }

    private fun startNetworkListening() {
        scope.launch {
            val shouldStart = listenMutex.withLock {
                if (isListening) false else { isListening = true; true }
            }
            if (!shouldStart) return@launch
            runListenLoop()
        }
    }

    private suspend fun runListenLoop() {
        var reconnectDelay = 1000L
        while (currentCoroutineContext().isActive) {
            // Регистрация идемпотентна: если устройство уже известно —
            // это просто чтение из хранилища.
            val deviceId = registerDeviceIfNeeded().getOrNull()

            if (deviceId == null) {
                // Устройство ещё не зарегистрировано (нет бэкенда или
                // пользователь не авторизован) — ждём с экспоненциальной
                // задержкой, чтобы не долбить сервер и не спамить логами.
                delay(reconnectDelay)
                reconnectDelay = minOf(reconnectDelay * 2, MAX_RECONNECT_DELAY_MS)
                continue
            }

            // Устройство известно — начинаем с быстрой попытки подключения.
            reconnectDelay = 1000L
            val config = syncConfigProvider.current()
            try {
                val token = tokenProvider.validToken()
                httpClient.webSocket(
                    request = {
                        wsUrl(config, PATH_WS_CLIPBOARD, mapOf(PARAM_DEVICE_ID to deviceId))
                        header(HttpHeaders.Authorization, bearer(token))
                    },
                ) {
                    reconnectDelay = 1000L
                    for (frame in incoming) {
                        if (frame !is Frame.Text) continue
                        val message = try {
                            json.decodeFromString(WebSocketMessage.serializer(), frame.readText())
                        } catch (e: Exception) {
                            Logger.w(tag, "Malformed WS frame", e)
                            null
                        }
                        if (message != null && message.isNewItem) {
                            fetchAndEmit(message.itemId!!)
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.w(tag, "WebSocket error, will retry in $reconnectDelay ms", e)
            } finally {
                delay(reconnectDelay)
                reconnectDelay = minOf(reconnectDelay * 2, MAX_RECONNECT_DELAY_MS)
            }
        }
    }

    /**
     * Докачивает payload элемента по `itemId` из WS-нотификации и эмитит
     * в поток. Страницы перебираются до [MAX_FETCH_PAGES]: сервер отдаёт
     * историю от свежих к старым, элемент обычно на первой странице.
     */
    private suspend fun fetchAndEmit(itemId: String) {
        for (page in 0 until MAX_FETCH_PAGES) {
            val config = syncConfigProvider.current()
            val item: ClipboardItemResponse = try {
                val token = tokenProvider.validToken()
                val response = httpClient.get {
                    restUrl(
                        config = config,
                        path = PATH_CLIPBOARD,
                        query = mapOf(
                            PARAM_PAGE to page.toString(),
                            PARAM_PAGE_SIZE to PAGE_SIZE.toString(),
                        ),
                    )
                    header(HttpHeaders.Authorization, bearer(token))
                }
                if (!response.status.isSuccess()) {
                    Logger.w(tag, "Fetch clipboards failed: ${response.status}")
                    return
                }
                val list = json.decodeFromString(ClipboardItemsListResponse.serializer(), response.bodyAsText())
                list.items.firstOrNull { it.id == itemId }
                    ?: if (list.items.isEmpty()) return else continue
            } catch (e: Exception) {
                Logger.w(tag, "Fetch clipboards error", e)
                return
            }

            _incomingItems.emit(
                EncryptedClipboardDto(
                    id = item.id,
                    encryptedText = item.payload,
                    timestamp = item.timestamp,
                    sourceDevice = item.deviceId,
                ),
            )
            return
        }
        Logger.w(tag, "Item $itemId not found in first $MAX_FETCH_PAGES pages")
    }

    private fun bearer(token: String): String = "Bearer $token"

    /**
     * REST-URL через Ktor URL-builder: строковые перегрузки `url(...)` в
     * Ktor 3.x удалены, остаётся только builder-вариант.
     */
    private fun io.ktor.client.request.HttpRequestBuilder.restUrl(
        config: SyncConfig,
        path: String,
        query: Map<String, String> = emptyMap(),
    ) {
        url {
            protocol = URLProtocol.createOrDefault(config.httpScheme)
            host = config.host
            port = config.port
            pathSegments = path.split("/").filter { it.isNotEmpty() }
            query.forEach { (key, value) -> parameters.append(key, value) }
        }
    }

    private fun io.ktor.client.request.HttpRequestBuilder.wsUrl(
        config: SyncConfig,
        path: String,
        query: Map<String, String> = emptyMap(),
    ) {
        url {
            protocol = if (config.secure) URLProtocol.WSS else URLProtocol.WS
            host = config.host
            port = config.port
            pathSegments = path.split("/").filter { it.isNotEmpty() }
            query.forEach { (key, value) -> parameters.append(key, value) }
        }
    }

    fun clear() {
        scope.cancel()
    }

    private companion object {
        const val PATH_CLIPBOARD = "api/v1/clipboard"
        const val PATH_DEVICES = "api/v1/devices"
        const val PATH_WS_CLIPBOARD = "ws/clipboard"
        const val PARAM_DEVICE_ID = "deviceId"
        const val PARAM_PAGE = "page"
        const val PARAM_PAGE_SIZE = "pageSize"
        const val MAX_FETCH_PAGES = 5
        const val PAGE_SIZE = 200
        const val MAX_RECONNECT_DELAY_MS = 30_000L
    }
}
