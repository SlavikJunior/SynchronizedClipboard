package com.github.slavikjunior.synchronizedclipboard.feature.devices.api

import kotlinx.coroutines.flow.Flow

/**
 * Источник данных для списка привязанных устройств.
 */
interface DevicesRepository {
    fun observeDevices(): Flow<List<DeviceItem>>

    suspend fun unlinkDevice(id: String): Result<Unit>
}
