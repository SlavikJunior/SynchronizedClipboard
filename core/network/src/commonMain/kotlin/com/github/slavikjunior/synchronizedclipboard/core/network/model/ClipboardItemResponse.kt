package com.github.slavikjunior.synchronizedclipboard.core.network.model

import kotlinx.serialization.Serializable

/**
 * Элемент истории из ответов бэкенда (`GET /api/v1/clipboard`,
 * WebSocket-нотификация несёт только часть полей).
 */
@Serializable
data class ClipboardItemResponse(
    val id: String,
    val deviceId: String,
    val userId: String,
    val timestamp: Long,
    val type: ClipboardType,
    val payload: String,
    val payloadSize: Int,
)
