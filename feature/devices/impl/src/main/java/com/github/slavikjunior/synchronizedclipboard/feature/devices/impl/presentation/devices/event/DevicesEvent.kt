package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.event

import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DeviceItem

internal sealed interface DevicesEvent {
    data class OnUnlinkClicked(val device: DeviceItem) : DevicesEvent
}
