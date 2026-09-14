package com.github.slavikjunior.synchronizedclipboard.core.network

import platform.Foundation.NSLog

internal actual object Logger {
    actual fun w(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            NSLog("[$tag] WARNING: $message | Error: ${throwable.message}")
        } else {
            NSLog("[$tag] WARNING: $message")
        }
    }
}
