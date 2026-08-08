package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.data.mapper

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.UserSettings
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.data.model.UserSettingsData

/**
 * Маппер между domain-моделью [UserSettings] и DTO [UserSettingsData].
 */
fun UserSettings.toData(): UserSettingsData = UserSettingsData(
    email = email,
    theme = theme,
    keepHistoryDays = keepHistoryDays,
)

fun UserSettingsData.toDomain(): UserSettings = UserSettings(
    email = email,
    theme = theme,
    keepHistoryDays = keepHistoryDays,
)
