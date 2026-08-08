package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.effect

internal sealed interface DevicesEffect {
    data class ShowToast(val message: String) : DevicesEffect
}
