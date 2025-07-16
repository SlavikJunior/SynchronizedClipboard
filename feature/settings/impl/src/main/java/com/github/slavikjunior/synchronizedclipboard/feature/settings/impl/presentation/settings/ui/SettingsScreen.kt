package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.BottomNavTab
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipBottomBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipEmptyView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipErrorView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipLoadingView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipScaffold
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipTopAppBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRoute
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DevicesRoute
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.navigation.SettingsRoute
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.R
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.effect.SettingsEffect
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.event.SettingsEvent
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.model.SettingsState
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.ui.components.HistoryDaysSelector
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.ui.components.ProfileCard
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.ui.components.ThemeSelector
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful-обёртка над [SettingsScreenContent].
 *
 * - State собирается через `collectAsStateWithLifecycle`.
 * - Effect собирается в `LaunchedEffect`.
 * - ViewModel резолвится через Koin `koinViewModel()`.
 */
@Composable
internal fun SettingsScreen(
    onNavigateToTab: (com.github.slavikjunior.synchronizedclipboard.core.navigation.Route) -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.LogoutCompleted -> onLogout()
                is SettingsEffect.ShowError -> {
                    snackbarHostState.showSnackbar(context.getString(effect.messageRes))
                }
            }
        }
    }

    SettingsScreenContent(
        state = screenState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::handleEvent,
        onNavigateToTab = onNavigateToTab,
    )
}

@Composable
private fun SettingsScreenContent(
    state: ScreenState<SettingsState>,
    snackbarHostState: SnackbarHostState,
    onEvent: (SettingsEvent) -> Unit,
    onNavigateToTab: (com.github.slavikjunior.synchronizedclipboard.core.navigation.Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {
        onNavigateToTab(ClipboardRoute)
    }

    SyncClipScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SyncClipTopAppBar(titleRes = R.string.settings_title) },
        bottomBar = {
            SyncClipBottomBar(
                currentRoute = SettingsRoute,
                tabs = listOf(
                    BottomNavTab(
                        route = ClipboardRoute,
                        selectedIcon = {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = null,
                            )
                        },
                        unselectedIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                            )
                        },
                    ),
                    BottomNavTab(
                        route = DevicesRoute,
                        selectedIcon = {
                            Icon(
                                imageVector = Icons.Filled.Devices,
                                contentDescription = null,
                            )
                        },
                        unselectedIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Devices,
                                contentDescription = null,
                            )
                        },
                    ),
                    BottomNavTab(
                        route = SettingsRoute,
                        selectedIcon = {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                            )
                        },
                        unselectedIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = null,
                            )
                        },
                    ),
                ),
                onNavigate = onNavigateToTab,
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        when (state) {
            ScreenState.Idle -> Unit
            ScreenState.Loading -> {
                SettingsLoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            is ScreenState.Success -> {
                SettingsSuccessContent(
                    settings = state.data,
                    onEvent = onEvent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            is ScreenState.Empty -> {
                SettingsEmptyContent(
                    message = state.message,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            is ScreenState.Error -> {
                SettingsErrorContent(
                    message = state.message,
                    onRetry = { /* TODO */ },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun SettingsLoadingContent(
    modifier: Modifier = Modifier,
) {
    SyncClipLoadingView(modifier = modifier.fillMaxSize())
}

@Preview(showBackground = true)
@Composable
private fun SettingsLoadingContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        SettingsLoadingContent()
    }
}

@Composable
private fun SettingsErrorContent(
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
private fun SettingsErrorContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        SettingsErrorContent(message = "Произошла ошибка", onRetry = {})
    }
}

@Composable
private fun SettingsEmptyContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    SyncClipEmptyView(
        message = message,
        icon = rememberVectorPainter(Icons.Filled.Settings),
        modifier = modifier.fillMaxSize(),
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsEmptyContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        SettingsEmptyContent(message = "Нет данных")
    }
}

@Composable
private fun SettingsSuccessContent(
    settings: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            ProfileCard(
                email = settings.email,
                onLogout = { onEvent(SettingsEvent.LogoutClicked) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.settings_theme_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            ThemeSelector(
                theme = settings.theme,
                onThemeChanged = { onEvent(SettingsEvent.ThemeChanged(it)) },
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.settings_history_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            HistoryDaysSelector(
                keepHistoryDays = settings.keepHistoryDays,
                onHistoryDaysChanged = { onEvent(SettingsEvent.HistoryDaysChanged(it)) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsSuccessContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        SettingsSuccessContent(
            settings = SettingsState(
                email = "user@example.com",
                theme = AppTheme.System,
                keepHistoryDays = 7,
            ),
            onEvent = {},
        )
    }
}
