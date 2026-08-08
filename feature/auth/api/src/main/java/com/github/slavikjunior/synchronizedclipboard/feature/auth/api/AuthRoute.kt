package com.github.slavikjunior.synchronizedclipboard.feature.auth.api

import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import kotlinx.serialization.Serializable

/**
 * Точка входа в flow авторизации.
 *
 * `@Serializable data object` — контракт Navigation 3 (см. AGENTS.md:
 * `@Serializable` на concrete data object, а не на interface).
 * Сериализуется для сохранения backStack через NavBackStackSerializer.
 *
 * `tabTitleRes = null` — Auth экран не входит в BottomNav.
 */
@Serializable
data object AuthRoute : Route {
    override val tabTitleRes: Int? = null
}
