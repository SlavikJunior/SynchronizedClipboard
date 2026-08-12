package com.github.slavikjunior.synchronizedclipboard.core.network.model

import kotlinx.serialization.Serializable

/**
 * DTO для передачи зашифрованного элемента буфера обмена по WebSocket.
 *
 * **Сервер получает ТОЛЬКО зашифрованные данные (E2E).** Расшифровка происходит
 * на клиенте через [com.github.slavikjunior.synchronizedclipboard.core.crypto.CryptoManager].
 */
@Serializable
data class EncryptedClipboardDto(
    val id: String,
    val encryptedText: String,
    val timestamp: Long,
    val sourceDevice: String,
    val isPinned: Boolean = false,
)
