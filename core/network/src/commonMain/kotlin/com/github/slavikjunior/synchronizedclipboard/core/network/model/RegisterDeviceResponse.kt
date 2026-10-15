package com.github.slavikjunior.synchronizedclipboard.core.network.model

import kotlinx.serialization.Serializable

/**
 * Тело `POST /api/v1/devices`.
 * FREE-тариф бэкенда ограничивает число активных устройств (по умолчанию 2).
 */
@Serializable
data class RegisterDeviceRequest(
    val deviceName: String,
)

/**
 * Ответ `POST /api/v1/devices` (201 Created). `id` — UUID устройства,
 * который клиент хранит локально и подставляет в запросы.
 */
@Serializable
data class RegisterDeviceResponse(
    val id: String,
    val deviceName: String,
    val createdAt: Long,
    val fcmToken: String? = null,
)
