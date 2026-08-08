package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.data.model

import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DeviceItem
import kotlinx.serialization.Serializable

/**
 * DTO устройства для сериализации/десериализации.
 *
 * Используется на транспортном уровне (сеть, БД). В домене работаем с
 * [com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DeviceItem].
 */
@Serializable
data class DeviceItemData(
    val id: String,
    val name: String,
    val os: String,
    val isCurrentDevice: Boolean = false,
    val isOnline: Boolean = false,
    val lastSyncTimestamp: Long = 0L,
) {
    fun toDomain() = DeviceItem(id, name, os, isCurrentDevice, isOnline, lastSyncTimestamp)
}
