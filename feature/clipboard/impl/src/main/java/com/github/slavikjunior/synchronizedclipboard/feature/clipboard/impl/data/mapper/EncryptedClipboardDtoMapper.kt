package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data.mapper

import com.github.slavikjunior.synchronizedclipboard.core.database.entity.ClipboardItemEntity
import com.github.slavikjunior.synchronizedclipboard.core.network.model.EncryptedClipboardDto

/**
 * Маппер между сущностью Room [ClipboardItemEntity] и сетевым DTO [EncryptedClipboardDto].
 *
 * Оба типа работают с зашифрованным текстом. Расшифровка/шифрование выполняется
 * отдельно в репозитории через [CryptoManager].
 */
fun ClipboardItemEntity.toDto() = EncryptedClipboardDto(
    id = id,
    encryptedText = encryptedText,
    timestamp = timestamp,
    sourceDevice = sourceDevice,
    isPinned = isPinned,
)

fun EncryptedClipboardDto.toEntity() = ClipboardItemEntity(
    id = id,
    encryptedText = encryptedText,
    timestamp = timestamp,
    sourceDevice = sourceDevice,
    isPinned = isPinned,
)
