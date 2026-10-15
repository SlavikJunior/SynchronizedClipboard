package com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model

data class UserSettings(
    val email: String,
    val theme: AppTheme,
    val keepHistoryDays: Int,
    /**
     * Парольная фраза для E2E-шифрования: PBKDF2-ключ деривируется из неё
     * одинаково на всех устройствах (Android/iOS/desktop). Пустая строка —
     * фраза не задана, шифрование работает в переходном device-local режиме.
     */
    val passphrase: String = "",
)
