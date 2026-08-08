package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl

internal sealed interface DevicesEffect {
    data class ShowToast(val message: String) : DevicesEffect
}
