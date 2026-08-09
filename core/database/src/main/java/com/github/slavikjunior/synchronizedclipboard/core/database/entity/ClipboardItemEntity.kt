package com.github.slavikjunior.synchronizedclipboard.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность буфера обмена в локальной базе данных Room.
 *
 * Хранит только зашифрованный текст. Расшифровка выполняется в репозитории
 * через [com.github.slavikjunior.synchronizedclipboard.core.crypto.CryptoManager].
 */
@Entity(tableName = "clipboard_items")
data class ClipboardItemEntity(
    @PrimaryKey
    val id: String,
    val encryptedText: String,
    val timestamp: Long,
    val sourceDevice: String,
    val isPinned: Boolean = false,
)
