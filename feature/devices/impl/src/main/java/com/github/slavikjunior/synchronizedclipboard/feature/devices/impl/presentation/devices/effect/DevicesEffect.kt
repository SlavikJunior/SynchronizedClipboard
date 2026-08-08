package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.effect

internal sealed interface DevicesEffect {
    data object CannotUnlinkCurrent : DevicesEffect
    data class Unlinked(val deviceName: String) : DevicesEffect
    data object UnlinkFailed : DevicesEffect
}
