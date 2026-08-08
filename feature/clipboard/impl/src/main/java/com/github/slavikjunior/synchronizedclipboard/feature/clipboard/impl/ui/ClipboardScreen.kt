package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipScaffold
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.ui.components.ClipboardScreenContent
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.ClipboardEvent
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.ClipboardEffect
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.ClipboardViewModel
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipFab
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipTopAppBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import android.widget.Toast
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ClipboardScreen(
    viewModel: ClipboardViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ClipboardEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is ClipboardEffect.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = effect.actionLabel,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        effect.onAction?.invoke()
                    }
                }
            }
        }
    }

    ClipboardScreenContent(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::handleEvent,
    )
}
