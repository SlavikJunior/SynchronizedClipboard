package com.github.slavikjunior.synchronizedclipboard.core.network

internal actual object PlatformConfig {
    // iOS: localhost для симулятора, реальный IP для физического устройства
    actual val syncHost: String = "localhost"
    actual val syncPort: Int = 8080
}
