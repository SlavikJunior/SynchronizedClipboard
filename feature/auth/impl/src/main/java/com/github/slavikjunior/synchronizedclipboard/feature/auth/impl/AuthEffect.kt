package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl

/**
 * One-shot side effects в MVI-архитектуре auth-фичи.
 *
 * [AuthEffect] передаётся через [kotlinx.coroutines.flow.SharedFlow] / Channel
 * и потребляется ровно один раз (unlike State). Используется для навигации
 * и transient UI-сообщений, которые не должны сохраняться в StateFlow.
 *
 * `internal` — эффекты не покидают границы :feature:auth:impl.
 */
internal sealed interface AuthEffect {

    /**
     * Успешная авторизация — переходим к главному экрану.
     * Callback реализует вызывающий код (:app Nav3Host).
     */
    data object NavigateToMain : AuthEffect

    /**
     * Transient-сообщение об ошибке (Snackbar / Toast).
     * @param message человеко-читаемый текст
     */
    data class ShowError(val message: String) : AuthEffect
}
