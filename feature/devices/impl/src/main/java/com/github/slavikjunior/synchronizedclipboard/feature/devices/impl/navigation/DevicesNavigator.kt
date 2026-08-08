package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DevicesRoute
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.ui.DevicesScreen

/**
 * **Единственный публичный экспорт** :feature:devices:impl (контракт AGENTS.md).
 */
fun EntryProviderScope<NavKey>.devicesNavEntry(
    onNavigateToTab: (Route) -> Unit = {},
) {
    entry(DevicesRoute) {
        DevicesScreen(onNavigateToTab = onNavigateToTab)
    }
}
