package com.github.slavikjunior.synchronizedclipboard.feature.devices.api

/**
 * Доменная модель устройства.
 *
 * **Чистая Kotlin-модель** — без фреймворк-аннотаций (`@Serializable`, `@Entity` и т.д.).
 * Для сериализации используется [com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.data.model.DeviceItemData]
 * в :impl модуле.
 */
data class DeviceItem(
    val id: String,
    val name: String,
    val os: String,
    val isCurrentDevice: Boolean = false,
    val isOnline: Boolean = false,
    val lastSyncTimestamp: Long = 0L,
)
