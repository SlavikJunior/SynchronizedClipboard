package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipScaffold
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.DevicesEvent
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.DevicesEffect
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.DevicesViewModel
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipTopAppBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.ui.components.DevicesScreenContent
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun DevicesScreen(
    onNavigateToTab: (com.github.slavikjunior.synchronizedclipboard.core.navigation.Route) -> Unit = {},
    viewModel: DevicesViewModel = koinViewModel(),
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DevicesEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DevicesScreenContent(
        state = screenState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::handleEvent,
        onNavigateToTab = onNavigateToTab,
    )
}
