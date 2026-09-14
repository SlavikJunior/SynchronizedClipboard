package com.github.slavikjunior.synchronizedclipboard.core.network

/**
 * Platform-agnostic logger для KMP.
 * Android → android.util.Log, iOS → NSLog/print
 */
internal expect object Logger {
    fun w(tag: String, message: String, throwable: Throwable? = null)
}
