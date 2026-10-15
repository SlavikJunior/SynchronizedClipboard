package com.github.slavikjunior.synchronizedclipboard.core.network

import android.os.Build

internal actual fun deviceName(): String = Build.MODEL ?: "android-device"
