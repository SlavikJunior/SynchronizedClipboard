package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.model

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.UserSettings

/**
 * Флаг активной записи в DataStore. Позволяет UI показывать индикатор
 * "сохранение..." для конкретного поля (theme или historyDays), не блокируя
 * весь экран. После успеха/ошибки снимается.
 */
internal enum class SaveInFlight {
    None,
    Theme,
    HistoryDays,
    Passphrase,
}

/**
 * UI-стейт экрана настроек.
 *
 * Инкапсулирует данные для отображения: профиль, выбранная тема,
 * срок хранения истории, парольная фраза. Создаётся из
 * [com.github.slavikjunior.synchronizedclipboard.feature.settings.api.UserSettings]
 * в ViewModel.
 */
internal data class SettingsState(
    val email: String = "",
    val theme: AppTheme = AppTheme.System,
    val keepHistoryDays: Int = 7,
    val passphrase: String = "",
    val saveInFlight: SaveInFlight = SaveInFlight.None,
) {
    companion object {
        fun from(settings: UserSettings): SettingsState =
            SettingsState(
                email = settings.email,
                theme = settings.theme,
                keepHistoryDays = settings.keepHistoryDays,
                passphrase = settings.passphrase,
            )
    }
}
