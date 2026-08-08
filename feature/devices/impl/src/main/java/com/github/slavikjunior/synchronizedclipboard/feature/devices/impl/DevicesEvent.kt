package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl

import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DeviceItem

internal sealed interface DevicesEvent {
    data class OnUnlinkClicked(val device: DeviceItem) : DevicesEvent
}
