package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data.mapper

import com.github.slavikjunior.synchronizedclipboard.core.database.entity.ClipboardItemEntity
import com.github.slavikjunior.synchronizedclipboard.core.network.model.EncryptedClipboardDto

/**
 * Маппер входящего сетевого DTO [EncryptedClipboardDto] в сущность Room
 * [ClipboardItemEntity]. Оба типа работают с зашифрованным текстом.
 *
 * Обратное направление (entity → DTO) не требуется: отправка идёт через
 * [com.github.slavikjunior.synchronizedclipboard.core.network.api.NetworkSyncApi.sendItem]
 * по «голому» payload-стрингу.
 */
fun EncryptedClipboardDto.toEntity() = ClipboardItemEntity(
    id = id,
    encryptedText = encryptedText,
    timestamp = timestamp,
    sourceDevice = sourceDevice,
    isPinned = isPinned,
)
