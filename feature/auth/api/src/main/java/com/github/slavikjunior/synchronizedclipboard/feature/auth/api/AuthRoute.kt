package com.github.slavikjunior.synchronizedclipboard.feature.auth.api

import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import kotlinx.serialization.Serializable

/**
 * Точка входа в flow авторизации.
 *
 * `@Serializable data object` — контракт Navigation 3 (см. AGENTS.md:
 * `@Serializable` на concrete data object, а не на interface).
 * Сериализуется для сохранения backStack через NavBackStackSerializer.
 */
@Serializable
data object AuthRoute : Route {
    // Auth экран не входит в BottomNav — оставляем пустой заголовок.
    override val tabTitle: String = ""
}
