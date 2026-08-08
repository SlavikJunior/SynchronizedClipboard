package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipErrorView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipLoadingView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipScaffold
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipTopAppBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.AuthEffect
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.AuthEvent
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.AuthFormState
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * **Stateless** Composable — рендерит экран по [screenState].
 *
 * Обёртка в `SyncClipScaffold` + `when` по [ScreenState] с переиспользуемыми
 * view из :core:designsystem (Loading / Error). Форма — Material3 primitives
 * (wrapper-ы для TextField/Button в :core:designsystem пока не созданы).
 *
 * `internal` — вызывается только из [AuthScreen] в том же модуле.
 */
@Composable
internal fun AuthScreenContent(
    screenState: ScreenState<AuthFormState>,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    SyncClipScaffold(
        modifier = modifier,
        topBar = { SyncClipTopAppBar(title = "Вход в SynchronizedClipboard") },
    ) { innerPadding: PaddingValues ->
        when (screenState) {
            is ScreenState.Loading ->
                SyncClipLoadingView(Modifier.padding(innerPadding))

            is ScreenState.Error ->
                SyncClipErrorView(
                    message = screenState.message,
                    onRetry = { onEvent(AuthEvent.SignInClick) },
                    modifier = Modifier.padding(innerPadding),
                )

            is ScreenState.Success ->
                AuthForm(
                    formState = screenState.data,
                    onEvent = onEvent,
                    modifier = Modifier.padding(innerPadding),
                )

            // Idle / Empty — не используются для auth-формы
            is ScreenState.Idle,
            is ScreenState.Empty -> Unit
        }
    }
}

/**
 * Stateful-обёртка над [AuthScreenContent].
 *
 * - `State` (ScreenState) собирается через `collectAsStateWithLifecycle` (lifecycle-aware).
 * - `Effect` (one-shot) собирается в `LaunchedEffect`.
 * - ViewModel резолвится через Koin `koinViewModel()`.
 *
 * `internal` — используется только в `authNavEntry()` (тот же модуль).
 */
@Composable
internal fun AuthScreen(
    onSignedIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: AuthViewModel = koinViewModel()
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    // One-shot Effect: NavigateToMain / ShowError
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AuthEffect.NavigateToMain -> onSignedIn()
                is AuthEffect.ShowError -> {}  // TODO: Snackbar через Scaffold отложен
            }
        }
    }

    AuthScreenContent(
        screenState = screenState,
        onEvent = viewModel::handleEvent,
        modifier = modifier,
    )
}

/**
 * Stateless form: два поля ввода + две кнопки.
 *
 * Material3 primitives (OutlinedTextField, Button) — обёртки SyncClip* для
 * input-компонентов будут добавлены в :core:designsystem в отдельной задаче.
 */
@Composable
private fun AuthForm(
    formState: AuthFormState,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = formState.login,
            onValueChange = { onEvent(AuthEvent.LoginChanged(it)) },
            label = { Text("Логин / Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = formState.password,
            onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
            label = { Text("Пароль") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { onEvent(AuthEvent.SignInClick) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Войти")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onEvent(AuthEvent.SignInWithGoogleClick) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Войти через Google")
        }
    }
}
