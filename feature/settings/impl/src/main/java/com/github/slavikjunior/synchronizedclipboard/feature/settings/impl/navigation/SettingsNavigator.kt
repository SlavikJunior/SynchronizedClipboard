package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.navigation.SettingsRoute
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.ui.SettingsScreen

/**
 * Единственный публичный экспорт модуля `:feature:settings:impl`.
 *
 * Регистрирует [SettingsRoute] в Navigation 3 `entryProvider`.
 */
fun EntryProviderScope<NavKey>.settingsNavEntry(
    onNavigateToTab: (Route) -> Unit = {},
    onLogout: () -> Unit = {},
) {
    entry(SettingsRoute) {
        SettingsScreen(
            onNavigateToTab = onNavigateToTab,
            onLogout = onLogout,
        )
    }
}
