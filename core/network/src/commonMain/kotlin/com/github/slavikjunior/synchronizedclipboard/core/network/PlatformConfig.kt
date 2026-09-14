package com.github.slavikjunior.synchronizedclipboard.core.network

/**
 * Platform-specific build configuration.
 * Android → BuildConfig (generated), iOS → hardcoded values
 */
internal expect object PlatformConfig {
    val syncHost: String
    val syncPort: Int
}
