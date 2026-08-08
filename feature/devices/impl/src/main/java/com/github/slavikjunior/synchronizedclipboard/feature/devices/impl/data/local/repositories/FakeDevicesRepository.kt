package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.data

import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DeviceItem
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DevicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Single

@Single
internal class FakeDevicesRepository : DevicesRepository {
    private val _devices = MutableStateFlow(
        listOf(
            DeviceItem(
                id = "current",
                name = "Pixel 8 Pro",
                os = "Android 15",
                isCurrentDevice = true,
                isOnline = true,
                lastSyncTimestamp = System.currentTimeMillis(),
            ),
            DeviceItem(
                id = "tablet",
                name = "Galaxy Tab S9",
                os = "Android 14",
                isCurrentDevice = false,
                isOnline = true,
                lastSyncTimestamp = System.currentTimeMillis() - 60_000,
            ),
            DeviceItem(
                id = "iphone",
                name = "iPhone 15 Pro",
                os = "iOS 18",
                isCurrentDevice = false,
                isOnline = false,
                lastSyncTimestamp = System.currentTimeMillis() - 3_600_000,
            ),
        ),
    )

    override fun observeDevices(): Flow<List<DeviceItem>> = _devices.asSharedFlow()

    override suspend fun unlinkDevice(id: String): Result<Unit> {
        return try {
            _devices.value = _devices.value.filter { it.id != id }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
