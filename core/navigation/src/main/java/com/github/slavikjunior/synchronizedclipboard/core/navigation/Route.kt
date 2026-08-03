package com.github.slavikjunior.synchronizedclipboard.core.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Базовый контракт навигации приложения SynchronizedClipboard.
 *
 * [Route] — **открытый** интерфейс (не sealed), потому что feature-модули
 * (`:feature:auth`, `:feature:clipboard`, ...) объявляют свои маршруты в
 * **отдельных Gradle-модулях**. Sealed-иерархия требует всех наследников
 * в одном source set, что нарушило бы независимость feature-модулей (тз.txt).
 *
 * ## Требования к конкретным Route
 *
 * Каждый Route-ключ в `:feature:api` обязан:
 * 1) Реализовывать [NavKey] (транзитивно через [Route]) — для Jetpack Navigation 3 backStack.
 * 2) Быть `@Serializable` **на самом классе/объекте**, а не на интерфейсе [Route]:
 *
 * ```kotlin
 * // :feature:clipboard:api
 * @Serializable
 * data object ClipboardRoute : Route
 *
 * @Serializable
 * data class DeviceDetailRoute(val deviceId: String) : Route
 * ```
 *
 * `@Serializable` на non-sealed интерфейсе невозможен (ошибка компиляции
 * kotlinx-serialization: "polymorphically serializable by default").
 * Сериализация конкретных Route работает на уровне их собственных сгенерированных
 * serializer-ов. Для сохранения backStack через `rememberNavBackStack(saver = ...)`
 * передаётся явно типизированный serializer каждого Route.
 *
 * Контракт строгий (см. AGENTS.md): только `data object` / `data class` с `@Serializable`.
 */
interface Route : NavKey
