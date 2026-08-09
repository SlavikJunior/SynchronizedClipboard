package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data.mapper

import com.github.slavikjunior.synchronizedclipboard.core.database.entity.ClipboardItemEntity
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem

/**
 * Маппер между сущностью Room [ClipboardItemEntity] и доменной моделью [ClipboardItem].
 *
 * Поле [ClipboardItemEntity.encryptedText] отображается в [ClipboardItem.text].
 * Расшифровка выполняется отдельно в репозитории через [com.github.slavikjunior.synchronizedclipboard.core.crypto.CryptoManager].
 */
fun ClipboardItemEntity.toDomain() = ClipboardItem(
    id = id,
    text = encryptedText,
    timestamp = timestamp,
    sourceDevice = sourceDevice,
    isPinned = isPinned,
)

fun ClipboardItem.toEntity() = ClipboardItemEntity(
    id = id,
    encryptedText = text,
    timestamp = timestamp,
    sourceDevice = sourceDevice,
    isPinned = isPinned,
)
