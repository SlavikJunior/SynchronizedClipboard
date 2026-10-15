package com.github.slavikjunior.synchronizedclipboard.core.network

/**
 * Локальная идентичность устройства: UUID, который бэкенд выдаёт при
 * `POST /api/v1/devices`. Хранилище — клиентское (DataStore на Android,
 * конфиг-файл на desktop), поэтому интерфейс suspend'ный.
 */
interface DeviceIdentityProvider {
    suspend fun currentDeviceId(): String?
    suspend fun saveDeviceId(deviceId: String)
}
