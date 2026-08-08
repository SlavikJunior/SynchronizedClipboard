package com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model

data class UserSettings(
    val email: String,
    val theme: AppTheme,
    val keepHistoryDays: Int,
)
