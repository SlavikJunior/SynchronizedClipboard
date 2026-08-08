package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.slavikjunior.synchronizedclipboard.feature.auth.api.AuthRoute
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.presentation.auth.ui.AuthScreen

/**
 * **Единственный публичный экспорт** :feature:auth:impl (контракт AGENTS.md).
 *
 * Регистрирует [AuthRoute] в `entryProvider { }` Navigation 3 через `entry(AuthRoute)`.
 * Всё остальное — `internal`: `AuthScreen`, `AuthViewModel`, форма и use case'ы.
 *
 * ## Использование в :app (Nav3Host)
 *
 * ```kotlin
 * NavDisplay(
 *     backStack = backStack,
 *     entryProvider = entryProvider {
 *         authNavEntry(onSignedIn = { router.push(MainRoute) })
 *     }
 * )
 * ```
 */
fun EntryProviderScope<NavKey>.authNavEntry(
    onSignedIn: () -> Unit,
) {
    entry(AuthRoute) {
        AuthScreen(onSignedIn = onSignedIn)
    }
}
