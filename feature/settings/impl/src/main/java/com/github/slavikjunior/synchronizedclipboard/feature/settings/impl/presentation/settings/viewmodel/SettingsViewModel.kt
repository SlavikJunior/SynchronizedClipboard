package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.LogoutUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.ObserveSettingsUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.UpdateThemeUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.R
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.effect.SettingsEffect
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.event.SettingsEvent
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.model.SettingsState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

/**
 * MVI-ViewModel фичи Settings.
 *
 * **State**: `StateFlow<ScreenState<SettingsState>>`
 * **Effect**: `Channel<SettingsEffect>`
 * **Event**: `handleEvent(SettingsEvent)` → dispatch на приватные `on*`-методы.
 */
@KoinViewModel
internal class SettingsViewModel(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ScreenState<SettingsState>>(ScreenState.Loading)
    val state = _state.asStateFlow()

    private val _effect = Channel<SettingsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeSettingsUseCase().collect { settings ->
                _state.value = ScreenState.Success(SettingsState.from(settings))
            }
        }
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ThemeChanged -> onThemeChanged(event)
            is SettingsEvent.HistoryDaysChanged -> onHistoryDaysChanged(event)
            SettingsEvent.LogoutClicked -> onLogoutClicked()
        }
    }

    private fun onThemeChanged(event: SettingsEvent.ThemeChanged) {
        viewModelScope.launch {
            updateThemeUseCase(event.theme).fold(
                onSuccess = { /* state обновится через observeSettings */ },
                onFailure = {
                    _effect.send(SettingsEffect.ShowError(R.string.settings_error_generic))
                },
            )
        }
    }

    private fun onHistoryDaysChanged(event: SettingsEvent.HistoryDaysChanged) {
        viewModelScope.launch {
            // TODO: реализовать UseCase сохранения keepHistoryDays
            val current = (_state.value as? ScreenState.Success)?.data ?: SettingsState()
            _state.value = ScreenState.Success(current.copy(keepHistoryDays = event.days))
        }
    }

    private fun onLogoutClicked() {
        viewModelScope.launch {
            logoutUseCase().fold(
                onSuccess = { _effect.send(SettingsEffect.LogoutCompleted) },
                onFailure = {
                    _effect.send(SettingsEffect.ShowError(R.string.settings_error_generic))
                },
            )
        }
    }
}
