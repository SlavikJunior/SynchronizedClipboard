package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.R as DesignR
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.BottomNavTab
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipAlertDialog
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipBottomBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipEmptyView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipErrorView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipLoadingView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipScaffold
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipTopAppBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRoute
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DeviceItem
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DevicesRoute
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.navigation.SettingsRoute
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.R
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.effect.DevicesEffect
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.event.DevicesEvent
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.model.DevicesState
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.ui.components.DeviceCard
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.viewmodel.DevicesViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful-обёртка над [DevicesScreenContent].
 *
 * - `State` собирается через `collectAsStateWithLifecycle`.
 * - `Effect` (one-shot) собирается в `LaunchedEffect`.
 * - ViewModel резолвится через Koin `koinViewModel()`.
 */
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
                is DevicesEffect.CannotUnlinkCurrent -> {
                    snackbarHostState.showSnackbar(context.getString(R.string.devices_cannot_unlink_current))
                }
                is DevicesEffect.Unlinked -> {
                    snackbarHostState.showSnackbar(context.getString(R.string.devices_unlinked, effect.deviceName))
                }
                is DevicesEffect.UnlinkFailed -> {
                    snackbarHostState.showSnackbar(context.getString(R.string.devices_unlink_failed))
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

@Composable
private fun DevicesScreenContent(
    state: ScreenState<DevicesState>,
    snackbarHostState: SnackbarHostState,
    onEvent: (DevicesEvent) -> Unit,
    onNavigateToTab: (com.github.slavikjunior.synchronizedclipboard.core.navigation.Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showUnlinkDialog by remember { mutableStateOf(false) }
    var selectedDevice by remember { mutableStateOf<DeviceItem?>(null) }

    BackHandler {
        if (showUnlinkDialog) {
            showUnlinkDialog = false
            selectedDevice = null
        } else {
            onNavigateToTab(ClipboardRoute)
        }
    }

    if (showUnlinkDialog && selectedDevice != null) {
        SyncClipAlertDialog(
            title = stringResource(id = R.string.devices_unlink_confirm_title),
            message = stringResource(id = R.string.devices_unlink_confirm_message, selectedDevice!!.name),
            confirmText = stringResource(id = R.string.devices_unlink_confirm),
            dismissText = stringResource(id = R.string.devices_unlink_cancel),
            onConfirm = {
                onEvent(DevicesEvent.OnUnlinkClicked(selectedDevice!!))
                showUnlinkDialog = false
                selectedDevice = null
            },
            onDismiss = {
                showUnlinkDialog = false
                selectedDevice = null
            },
        )
    }

    SyncClipScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SyncClipTopAppBar(titleRes = R.string.devices_title) },
        bottomBar = {
            SyncClipBottomBar(
                currentRoute = DevicesRoute,
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
                                imageVector = Icons.Filled.ContentCopy,
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
                                imageVector = Icons.Filled.Devices,
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
                                imageVector = Icons.Filled.Settings,
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
                DevicesLoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            is ScreenState.Success -> {
                val devices = state.data.devices
                if (devices.isEmpty()) {
                    DevicesEmptyContent(
                        message = stringResource(id = R.string.devices_empty),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                } else {
                    DevicesSuccessContent(
                        devices = devices,
                        onUnlinkClick = { device ->
                            selectedDevice = device
                            showUnlinkDialog = true
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                }
            }

            is ScreenState.Empty -> {
                DevicesEmptyContent(
                    message = state.message,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            is ScreenState.Error -> {
                DevicesErrorContent(
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
private fun DevicesLoadingContent(
    modifier: Modifier = Modifier,
) {
    SyncClipLoadingView(modifier = modifier.fillMaxSize())
}

@Preview(showBackground = true)
@Composable
private fun DevicesLoadingContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        DevicesLoadingContent()
    }
}

@Composable
private fun DevicesErrorContent(
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
private fun DevicesErrorContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        DevicesErrorContent(message = "Произошла ошибка", onRetry = {})
    }
}

@Composable
private fun DevicesEmptyContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    SyncClipEmptyView(
        message = message,
        icon = painterResource(id = DesignR.drawable.ic_clipboard_empty),
        modifier = modifier.fillMaxSize(),
    )
}

@Preview(showBackground = true)
@Composable
private fun DevicesEmptyContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        DevicesEmptyContent(message = "Нет привязанных устройств")
    }
}

@Composable
private fun DevicesSuccessContent(
    devices: List<DeviceItem>,
    onUnlinkClick: (DeviceItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            count = devices.size,
            key = { index -> devices[index].id },
        ) { index ->
            val device = devices[index]
            DeviceCard(
                device = device,
                onUnlink = { onUnlinkClick(device) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DevicesSuccessContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        DevicesSuccessContent(
            devices = listOf(
                DeviceItem(
                    id = "1",
                    name = "Pixel 8 Pro",
                    os = "Android 15",
                    isCurrentDevice = true,
                    isOnline = true,
                    lastSyncTimestamp = System.currentTimeMillis(),
                ),
                DeviceItem(
                    id = "2",
                    name = "iPhone 15",
                    os = "iOS 17",
                    isCurrentDevice = false,
                    isOnline = false,
                    lastSyncTimestamp = System.currentTimeMillis(),
                ),
            ),
            onUnlinkClick = {},
        )
    }
}
