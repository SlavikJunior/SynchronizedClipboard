package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data.model

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import kotlinx.serialization.Serializable

/**
 * DTO элемента буфера обмена для сериализации/десериализации.
 *
 * Используется на транспортном уровне (сеть, БД). В домене работаем с
 * [com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem].
 */
@Serializable
data class ClipboardItemData(
    val id: String,
    val text: String,
    val timestamp: Long,
    val sourceDevice: String,
    val isPinned: Boolean = false,
) {
    fun toDomain() = ClipboardItem(id, text, timestamp, sourceDevice, isPinned)
}
