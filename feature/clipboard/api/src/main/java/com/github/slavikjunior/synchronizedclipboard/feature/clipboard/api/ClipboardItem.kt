package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api

/**
 * Доменная модель элемента буфера обмена.
 *
 * **Чистая Kotlin-модель** — без фреймворк-аннотаций (`@Serializable`, `@Entity` и т.д.).
 * Для сериализации используется [com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data.model.ClipboardItemData]
 * в :impl модуле.
 */
data class ClipboardItem(
    val id: String,
    val text: String,
    val timestamp: Long,
    val sourceDevice: String,
    val isPinned: Boolean = false,
)
