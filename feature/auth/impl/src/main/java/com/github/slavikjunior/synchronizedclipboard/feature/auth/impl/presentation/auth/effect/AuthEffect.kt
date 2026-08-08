package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.presentation.auth.effect

import androidx.annotation.StringRes

internal sealed interface AuthEffect {

    /**
     * Успешная авторизация — переходим к главному экрану.
     * Callback реализует вызывающий код (:app Nav3Host).
     */
    data object NavigateToMain : AuthEffect

    /**
     * Transient-сообщение об ошибке (Snackbar / Toast).
     * @param messageRes человеко-читаемый текст
     */
    data class ShowError(@StringRes val messageRes: Int) : AuthEffect
}
