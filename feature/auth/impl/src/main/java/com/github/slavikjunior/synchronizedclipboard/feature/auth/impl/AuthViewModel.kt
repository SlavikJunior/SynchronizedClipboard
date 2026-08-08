package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.auth.api.SignInUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.auth.api.SignInWithGoogleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

/**
 * MVI-ViewModel auth-фичи.
 *
 * **State**: `StateFlow<ScreenState<AuthFormState>>` — ScreenState из :core:designsystem
 * (Idle → Success форма / Loading / Error).
 * **Effect**: `SharedFlow<AuthEffect>` через Channel — one-shot (navigate, snackbar).
 * **Intent**: `handleEvent(AuthEvent)` — синхронный reducer.
 *
 * `@KoinViewModel` — Koin Compiler Plugin 4.x регистрирует фабрику в сгенерированном
 * Koin-модуле. Зависимости UseCase резолвятся из Koin graph через
 * `@Single`-аннотированные impl в [AuthModule] (через @ComponentScan).
 *
 * `internal` — ViewModel не покидает :feature:auth:impl; извлекается в Compose через
 * `koinViewModel<AuthViewModel>()` внутри модуля (internal-видимость достаточна).
 */
@KoinViewModel
internal class AuthViewModel(
    private val signInUseCase: SignInUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    /** Атомарный StateFlow для UI (MVI: single source of truth). */
    private val _state = MutableStateFlow<ScreenState<AuthFormState>>(ScreenState.Idle)
    val state = _state.asStateFlow()

    /** Channel для one-shot Effect (MVI: SideEffect). */
    private val _effect = Channel<AuthEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        // Форма готова сразу — переводим из Idle в Success пустой формой.
        _state.value = ScreenState.Success(AuthFormState())
    }

    /**
     * Точка входа для всех UI-событий (Intent → Reduce).
     * Синхронно обновляет State; асинхронные side effect идут через Effect/Loading.
     */
    fun handleEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.LoginChanged      -> updateForm { it.copy(login = event.login) }
            is AuthEvent.PasswordChanged   -> updateForm { it.copy(password = event.password) }
            is AuthEvent.SignInClick       -> signIn()
            is AuthEvent.SignInWithGoogleClick -> signInWithGoogle()
        }
    }

    /** Обновление кэшированной формы и переход в Success. */
    private fun updateForm(reducer: (AuthFormState) -> AuthFormState) {
        val current = (state.value as? ScreenState.Success)?.data ?: formCache
        val updated = reducer(current)
        formCache = updated
        _state.value = ScreenState.Success(updated)
    }

    /** Форма кэшируется при переходе в Loading/Error, чтобы восстановить после ошибки. */
    private var formCache: AuthFormState = AuthFormState()

    private fun signIn() {
        val form = formCache
        viewModelScope.launch {
            _state.value = ScreenState.Loading
            val result = signInUseCase(form.login, form.password)
            result.fold(
                onSuccess = { _effect.send(AuthEffect.NavigateToMain) },
                onFailure = {
                    _state.value = ScreenState.Error(
                        it.message ?: "Ошибка входа"
                    )
                },
            )
        }
    }

    private fun signInWithGoogle() {
        viewModelScope.launch {
            _state.value = ScreenState.Loading
            val result = signInWithGoogleUseCase()
            result.fold(
                onSuccess = { _effect.send(AuthEffect.NavigateToMain) },
                onFailure = {
                    _state.value = ScreenState.Error(
                        it.message ?: "Ошибка входа через Google"
                    )
                },
            )
        }
    }
}
