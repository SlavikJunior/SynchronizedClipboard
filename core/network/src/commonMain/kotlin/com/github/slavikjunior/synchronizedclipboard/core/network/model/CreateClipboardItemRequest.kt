package com.github.slavikjunior.synchronizedclipboard.core.network.model

import kotlinx.serialization.Serializable

/**
 * Тело `POST /api/v1/clipboard`.
 *
 * `payload` — строка, которую сервер хранит не глядя (blind relay E2EE).
 * Клиент кладёт туда результат `Base64(IV):Base64(cipherText)`.
 *
 * @property deviceId UUID устройства-отправителя (из регистрации).
 * @property type тип контента.
 * @property payload зашифрованная строка.
 */
@Serializable
data class CreateClipboardItemRequest(
    val deviceId: String,
    val type: ClipboardType,
    val payload: String,
)
