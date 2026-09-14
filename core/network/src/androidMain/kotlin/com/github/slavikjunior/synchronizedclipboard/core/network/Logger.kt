package com.github.slavikjunior.synchronizedclipboard.core.network

import android.util.Log

internal actual object Logger {
    actual fun w(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.w(tag, message, throwable)
        } else {
            Log.w(tag, message)
        }
    }
}
