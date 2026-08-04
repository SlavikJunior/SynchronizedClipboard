package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl

/**
 * Intent / Action в MVI-архитектуре auth-фичи.
 *
 * Неизменяемый sealed interface: ViewModel принимает [AuthEvent] в [AuthViewModel.handleEvent]
 * и чисто синхронно обновляет State / Effect (MVI: Intent → Reduce → State + Effect).
 *
 * `internal` — события не покидают границы :feature:auth:impl.
 */
internal sealed interface AuthEvent {

    /**
     * Обновление поля логина во время ввода.
     * @param login текущий текст в OutlinedTextField
     */
    data class LoginChanged(val login: String) : AuthEvent

    /**
     * Обновление поля пароля во время ввода.
     * @param password текущий текст в OutlinedTextField
     */
    data class PasswordChanged(val password: String) : AuthEvent

    /**
     * Клик по кнопке «Войти» (email + password).
     */
    data object SignInClick : AuthEvent

    /**
     * Клик по кнопке «Войти через Google».
     */
    data object SignInWithGoogleClick : AuthEvent
}
