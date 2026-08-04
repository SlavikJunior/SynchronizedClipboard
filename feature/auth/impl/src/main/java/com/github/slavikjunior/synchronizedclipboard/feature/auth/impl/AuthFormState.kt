package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl

/**
 * Состояние формы ввода (логин/пароль).
 *
 * Инкапсулируется внутри [com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState.Success]
 * для передачи в UI через StateFlow<ScreenState<AuthFormState>>.
 *
 * `internal` — используется только внутри :feature:auth:impl.
 */
internal data class AuthFormState(
    val login: String = "",
    val password: String = "",
)
