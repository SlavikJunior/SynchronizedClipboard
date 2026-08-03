package com.github.slavikjunior.synchronizedclipboard.core.designsystem.state

/**
 * Унифицированное состояние экрана (контракт AGENTS.md).
 *
 * ViewModel любого feature-модуля экспонирует `StateFlow<UiState<T>>`,
 * router-level screen собирает его через `collectAsStateWithLifecycle()`
 * и делегирует stateless-композитору, который выбирает один из
 * переиспользуемых views из [com.github.slavikjunior.synchronizedclipboard.core.designsystem.components].
 *
 * Вариант-проекция [out T] позволяет [Success] ковариантно наследоваться от
 * конкретных моделей (например `Success(listOfItems)` при `ScreenState<List<Item>>`),
 * сохраняя при этом возможность `Loading` / `Empty` / `Error` / `Idle` нести `Nothing`.
 */
sealed interface ScreenState<out T> {
    /**
     * Экран в исходном / неиницииализированном запросом состоянии.
     * Используется редко — обычно до первого `collect`.
     */
    data object Idle : ScreenState<Nothing>

    /**
     * Загрузка данных: показываем [com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipLoadingView].
     */
    data object Loading : ScreenState<Nothing>

    /**
     * Данные успешно получены. Инкапсулирует доменную модель [data].
     */
    data class Success<T>(val data: T) : ScreenState<T>

    /**
     * Нет данных для отображения: показываем [com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipEmptyView].
     */
    data class Empty(val message: String) : ScreenState<Nothing>

    /**
     * Ошибка: показываем [com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipErrorView].
     * [message] человеко-читаемое сообщение (не рефлекс эксепшена).
     */
    data class Error(val message: String) : ScreenState<Nothing>
}
