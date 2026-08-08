package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl

import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DeviceItem

internal data class DevicesState(
    val devices: List<DeviceItem> = emptyList(),
)
