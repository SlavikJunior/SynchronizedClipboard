package com.github.slavikjunior.synchronizedclipboard.core.network

import platform.UIKit.UIDevice

internal actual fun deviceName(): String =
    UIDevice.currentDevice.name.ifBlank { "ios-device" }
