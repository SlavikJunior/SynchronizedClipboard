package com.github.slavikjunior.synchronizedclipboard.feature.devices.api

import kotlinx.serialization.Serializable

/**
 * Доменная модель устройства.
 */
@Serializable
data class DeviceItem(
    val id: String,
    val name: String,
    val os: String,
    val isCurrentDevice: Boolean = false,
    val isOnline: Boolean = false,
    val lastSyncTimestamp: Long = 0L,
)
