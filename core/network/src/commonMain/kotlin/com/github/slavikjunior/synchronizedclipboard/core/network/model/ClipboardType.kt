package com.github.slavikjunior.synchronizedclipboard.core.network.model

import kotlinx.serialization.Serializable

/**
 * Тип элемента буфера обмена. Значения совпадают с enum'ом бэкенда
 * — сериализуются по имени ("TEXT"/"IMAGE"/"FILE").
 *
 * MVP использует только [TEXT]: desktop- и Android-клиенты синхронизируют
 * текст. IMAGE/FILE сохранены для совместимости контракта.
 */
@Serializable
enum class ClipboardType {
    TEXT,
    IMAGE,
    FILE,
}
