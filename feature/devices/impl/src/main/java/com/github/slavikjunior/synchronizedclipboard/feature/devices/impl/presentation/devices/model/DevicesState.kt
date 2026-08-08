package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.model

import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DeviceItem

internal data class DevicesState(
    val devices: List<DeviceItem> = emptyList(),
)
