package com.github.slavikjunior.synchronizedclipboard.core.network

internal actual object PlatformConfig {
    // Android emulator: 10.0.2.2 = host machine's localhost
    actual val syncHost: String = "10.0.2.2"
    actual val syncPort: Int = 8080
}
