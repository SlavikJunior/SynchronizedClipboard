package com.github.slavikjunior.synchronizedclipboard.core.network.model

import kotlinx.serialization.Serializable

/**
 * Тело `GET /api/v1/clipboard?page&pageSize`.
 * Сервер клампит pageSize в 1–200, page >= 0.
 */
@Serializable
data class ClipboardItemsListResponse(
    val items: List<ClipboardItemResponse>,
    val page: Int,
    val pageSize: Int,
)
