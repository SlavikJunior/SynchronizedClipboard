package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRoute
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.ui.ClipboardScreen

/**
 * **Единственный публичный экспорт** :feature:clipboard:impl (контракт AGENTS.md).
 */
fun EntryProviderScope<NavKey>.clipboardNavEntry(
    onNavigateToTab: (Route) -> Unit = {},
) {
    entry(ClipboardRoute) {
        ClipboardScreen(onNavigateToTab = onNavigateToTab)
    }
}
