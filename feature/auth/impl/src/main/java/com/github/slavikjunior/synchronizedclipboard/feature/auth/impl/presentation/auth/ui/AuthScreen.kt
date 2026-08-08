package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.presentation.auth.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipErrorView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipLoadingView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipScaffold
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipTopAppBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.R
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.presentation.auth.effect.AuthEffect
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.presentation.auth.event.AuthEvent
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.presentation.auth.model.AuthFormState
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.presentation.auth.viewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

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
                is AuthEffect.ShowError -> {
                    // TODO: Snackbar через Scaffold отложен
                }
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
 * Stateless Composable — рендерит экран по [screenState].
 *
 * Обёртка в `SyncClipScaffold` + `when` по [ScreenState] с переиспользуемыми
 * view из :core:designsystem (Loading / Error). Форма — Material3 primitives
 * (wrapper-ы для TextField/Button в :core:designsystem пока не созданы).
 *
 * `internal` — вызывается только из [AuthScreen] в том же модуле.
 */
@Composable
private fun AuthScreenContent(
    screenState: ScreenState<AuthFormState>,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    SyncClipScaffold(
        modifier = modifier,
        topBar = { SyncClipTopAppBar(titleRes = R.string.auth_title) },
    ) { innerPadding: PaddingValues ->
        when (screenState) {
            is ScreenState.Loading ->
                AuthLoadingContent(modifier = Modifier.padding(innerPadding))

            is ScreenState.Error ->
                AuthErrorContent(
                    message = screenState.message,
                    onRetry = { onEvent(AuthEvent.SignInClick) },
                    modifier = Modifier.padding(innerPadding),
                )

            is ScreenState.Success ->
                AuthSuccessContent(
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

@Composable
private fun AuthLoadingContent(
    modifier: Modifier = Modifier,
) {
    SyncClipLoadingView(modifier = modifier.fillMaxSize())
}

@Preview(showBackground = true)
@Composable
private fun AuthLoadingContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        AuthLoadingContent()
    }
}

@Composable
private fun AuthErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SyncClipErrorView(
        message = message,
        onRetry = onRetry,
        modifier = modifier.fillMaxSize(),
    )
}

@Preview(showBackground = true)
@Composable
private fun AuthErrorContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        AuthErrorContent(message = "Произошла ошибка", onRetry = {})
    }
}

@Composable
private fun AuthSuccessContent(
    formState: AuthFormState,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AuthFormContent(
        formState = formState,
        onEvent = onEvent,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun AuthSuccessContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        AuthSuccessContent(
            formState = AuthFormState(login = "user@example.com", password = "password"),
            onEvent = {},
        )
    }
}

@Composable
private fun AuthEmptyContent(
    modifier: Modifier = Modifier,
) {
    // Не используется для auth-формы, оставлен для единообразия
    Spacer(modifier = modifier.fillMaxSize())
}

@Preview(showBackground = true)
@Composable
private fun AuthEmptyContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        AuthEmptyContent()
    }
}

/**
 * Stateless form: два поля ввода + две кнопки.
 *
 * Material3 primitives (OutlinedTextField, Button) — обёртки SyncClip* для
 * input-компонентов будут добавлены в :core:designsystem в отдельной задаче.
 */
@Composable
private fun AuthFormContent(
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
            label = { Text(text = stringResource(id = R.string.auth_label_login)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = formState.password,
            onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
            label = { Text(text = stringResource(id = R.string.auth_label_password)) },
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
            Text(text = stringResource(id = R.string.auth_button_signin))
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onEvent(AuthEvent.SignInWithGoogleClick) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(id = R.string.auth_button_google))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthFormContentPreview() {
    AuthFormContent(
        formState = AuthFormState(login = "user@example.com", password = "password"),
        onEvent = {},
    )
}
