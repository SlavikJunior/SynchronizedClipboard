package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.LogoutUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.ObserveSettingsUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.UpdateKeepHistoryDaysUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.UpdateThemeUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.R
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.effect.SettingsEffect
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.event.SettingsEvent
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.model.SaveInFlight
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.model.SettingsState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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
    private val updateKeepHistoryDaysUseCase: UpdateKeepHistoryDaysUseCase,
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
                _state.update { ScreenState.Success(SettingsState.from(settings)) }
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
        // Снимок текущего успешного state для возможного отката.
        val previous: SettingsState? = (state.value as? ScreenState.Success)?.data

        // Optimistic update — UI реагирует мгновенно, плюс флаг "сохранение".
        if (previous != null) {
            _state.update {
                ScreenState.Success(
                    previous.copy(
                        theme = event.theme,
                        saveInFlight = SaveInFlight.Theme,
                    ),
                )
            }
        }

        viewModelScope.launch {
            updateThemeUseCase(event.theme).fold(
                onSuccess = {
                    // Снимаем флаг записи. Значение theme в state уже консистентно
                    // с DataStore (init { observeSettingsUseCase() } подтвердит это
                    // при следующей эмиссии).
                    clearSaveInFlight()
                },
                onFailure = { cause ->
                    // Rollback к предыдущему значению.
                    if (previous != null) {
                        _state.update { ScreenState.Success(previous) }
                    }
                    _effect.send(SettingsEffect.ShowErrorWithMessageText(cause.message.orEmpty()))
                },
            )
        }
    }

    private fun onHistoryDaysChanged(event: SettingsEvent.HistoryDaysChanged) {
        // Снимок текущего успешного state для возможного отката.
        val previous: SettingsState? = (state.value as? ScreenState.Success)?.data

        // Optimistic update — UI реагирует мгновенно, плюс флаг "сохранение".
        if (previous != null) {
            _state.update {
                ScreenState.Success(
                    previous.copy(
                        keepHistoryDays = event.days,
                        saveInFlight = SaveInFlight.HistoryDays,
                    ),
                )
            }
        }

        viewModelScope.launch {
            updateKeepHistoryDaysUseCase(event.days).fold(
                onSuccess = {
                    // Снимаем флаг записи. Значение keepHistoryDays в state уже
                    // консистентно с DataStore (init { observeSettingsUseCase() }
                    // подтвердит это при следующей эмиссии).
                    clearSaveInFlight()
                },
                onFailure = { cause ->
                    // Rollback к предыдущему значению.
                    if (previous != null) {
                        _state.update { ScreenState.Success(previous) }
                    }
                    _effect.send(SettingsEffect.ShowErrorWithMessageText(cause.message.orEmpty()))
                },
            )
        }
    }

    /**
     * Снимает флаг активной записи в state, если state находится в [ScreenState.Success]
     * и `saveInFlight` отличен от [SaveInFlight.None]. Используется после успешной
     * записи в DataStore, когда сам `init { observeSettingsUseCase() }` подхватит
     * изменение. Корректно работает с `out`-вариантностью [ScreenState].
     */
    private fun clearSaveInFlight() {
        val current = _state.value
        if (current !is ScreenState.Success) return
        val data = current.data
        if (data.saveInFlight == SaveInFlight.None) return
        _state.update {
            ScreenState.Success(data.copy(saveInFlight = SaveInFlight.None))
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
