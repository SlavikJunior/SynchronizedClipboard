package com.github.slavikjunior.synchronizedclipboard.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Сообщение WebSocket `/ws/clipboard`.
 *
 * **Важно:** сервер присылает только метаданные —
 * `{"type":"NEW_ITEM","itemId":"...","timestamp":...,"itemType":"TEXT"}` —
 * без payload. Зашифрованный текст добирается отдельным
 * `GET /api/v1/clipboard` по `itemId` (см. KtorNetworkSyncApi).
 *
 * `ERROR`-сообщения бэкенда не присылает, поле оставлено для совместимости
 * и трактуется как no-op.
 */
@Serializable
data class WebSocketMessage(
    val type: String,
    @SerialName("itemId") val itemId: String? = null,
    val timestamp: Long = 0L,
    @SerialName("itemType") val itemType: ClipboardType? = null,
    val message: String? = null,
) {
    val isNewItem: Boolean get() = type == TYPE_NEW_ITEM && itemId != null

    companion object {
        const val TYPE_NEW_ITEM = "NEW_ITEM"
        const val TYPE_ERROR = "ERROR"
    }
}
