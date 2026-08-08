package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.R
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipBottomBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipEmptyView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipErrorView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipLoadingView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipScaffold
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipTopAppBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.BottomNavTab
import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRoute
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DevicesRoute
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DeviceItem
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.DevicesEvent
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.DevicesState

@Composable
internal fun DevicesScreenContent(
    state: ScreenState<DevicesState>,
    snackbarHostState: androidx.compose.material3.SnackbarHostState,
    onEvent: (DevicesEvent) -> Unit,
    onNavigateToTab: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentDevices = (state as? ScreenState.Success)?.data?.devices ?: emptyList()

    SyncClipScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SyncClipTopAppBar(title = "Мои устройства") },
        bottomBar = {
            SyncClipBottomBar(
                currentRoute = DevicesRoute,
                tabs = listOf(
                    BottomNavTab(
                        route = ClipboardRoute,
                        title = "Буфер",
                        selectedIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = null,
                            )
                        },
                        unselectedIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = null,
                            )
                        },
                    ),
                    BottomNavTab(
                        route = DevicesRoute,
                        title = "Устройства",
                        selectedIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.Devices,
                                contentDescription = null,
                            )
                        },
                        unselectedIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.Devices,
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
                SyncClipLoadingView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            is ScreenState.Success -> {
                val devices = state.data.devices
                if (devices.isEmpty()) {
                    SyncClipEmptyView(
                        message = "Нет привязанных устройств",
                        icon = androidx.compose.ui.res.painterResource(id = R.drawable.ic_clipboard_empty),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
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
                                onUnlink = { onEvent(DevicesEvent.OnUnlinkClicked(device)) },
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }
            }

            is ScreenState.Empty -> {
                SyncClipEmptyView(
                    message = state.message,
                    icon = androidx.compose.ui.res.painterResource(id = R.drawable.ic_clipboard_empty),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            is ScreenState.Error -> {
                SyncClipErrorView(
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
