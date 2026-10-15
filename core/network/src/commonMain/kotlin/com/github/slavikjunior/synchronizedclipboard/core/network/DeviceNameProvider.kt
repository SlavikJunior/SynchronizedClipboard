package com.github.slavikjunior.synchronizedclipboard.core.network

/**
 * Имя устройства для регистрации на бэкенде (уникально в рамках
 * пользователя, case-insensitive).
 */
internal expect fun deviceName(): String
