package com.github.slavikjunior.synchronizedclipboard.core.network

internal actual object Logger {
    actual fun w(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) {
            System.err.println("W/$tag: $message ${throwable.stackTraceToString()}")
        } else {
            System.err.println("W/$tag: $message")
        }
    }
}
