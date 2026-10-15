package com.github.slavikjunior.synchronizedclipboard.core.network

import java.net.InetAddress

internal actual fun deviceName(): String = try {
    InetAddress.getLocalHost().hostName.ifBlank { "desktop-device" }
} catch (e: Exception) {
    "desktop-device"
}
