package com.github.slavikjunior.synchronizedclipboard.core.network.model

import kotlinx.serialization.Serializable

/**
 * Клиентская модель входящего элемента буфера обмена.
 *
 * Транспортная точка зрения: payload уже расшифрован выше слоя (в
 * [com.github.slavikjunior.synchronizedclipboard.core.crypto.CryptoManager]),
 * поэтому поле называется `encryptedText` — это ровно та строка
 * `Base64(IV):Base64(cipherText)`, которую вернул бэкенд.
 *
 * @property sourceDevice UUID устройства-отправителя (`deviceId` из ответа);
 *                     UI использует его как человекочитаемую метку.
 * @property isPinned сервер не хранит закрепление — всегда false в MVP.
 */
@Serializable
data class EncryptedClipboardDto(
    val id: String,
    val encryptedText: String,
    val timestamp: Long,
    val sourceDevice: String,
    val isPinned: Boolean = false,
)
