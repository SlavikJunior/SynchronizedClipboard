package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.event

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme

/**
 * Пользовательские события (MVI Intent) экрана настроек.
 */
internal sealed interface SettingsEvent {
    data class ThemeChanged(val theme: AppTheme) : SettingsEvent
    data class HistoryDaysChanged(val days: Int) : SettingsEvent
    data object LogoutClicked : SettingsEvent
}
