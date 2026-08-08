package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.effect

/**
 * One-shot side-effects экрана настроек.
 */
internal sealed interface SettingsEffect {
    data object LogoutCompleted : SettingsEffect
    data class ShowError(val messageRes: Int) : SettingsEffect
}
