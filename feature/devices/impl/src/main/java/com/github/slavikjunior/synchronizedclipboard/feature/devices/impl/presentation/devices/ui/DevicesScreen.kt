package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.ui

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.R
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.BottomNavTab
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
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.effect.DevicesEffect
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.event.DevicesEvent
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.model.DevicesState
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

@Composable
private fun DevicesScreenContent(
    state: ScreenState<DevicesState>,
    snackbarHostState: SnackbarHostState,
    onEvent: (DevicesEvent) -> Unit,
    onNavigateToTab: (com.github.slavikjunior.synchronizedclipboard.core.navigation.Route) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                        message = "Нет привязанных устройств",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                } else {
                    DevicesSuccessContent(
                        devices = devices,
                        onEvent = onEvent,
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

@Composable
private fun DevicesEmptyContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    SyncClipEmptyView(
        message = message,
        icon = painterResource(id = R.drawable.ic_clipboard_empty),
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
private fun DevicesSuccessContent(
    devices: List<DeviceItem>,
    onEvent: (DevicesEvent) -> Unit,
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
                onUnlink = { onEvent(DevicesEvent.OnUnlinkClicked(device)) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun DeviceCard(
    device: DeviceItem,
    onUnlink: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (device.isCurrentDevice) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .padding(end = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = when (device.os) {
                                "iOS" -> Icons.Filled.Smartphone
                                "Android" -> Icons.Filled.PhoneAndroid
                                else -> Icons.Filled.Computer
                            },
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = device.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        StatusDot(isOnline = device.isOnline)
                        Text(
                            text = device.os,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        if (device.isCurrentDevice) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            ) {
                                Text(
                                    text = "Это устройство",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                }
            }

            if (!device.isCurrentDevice) {
                IconButton(onClick = onUnlink) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Отвязать устройство",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusDot(isOnline: Boolean) {
    val color = if (isOnline) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Canvas(
        modifier = Modifier.size(8.dp),
    ) {
        drawCircle(color = color)
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceCardPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        DeviceCard(
            device = DeviceItem(
                id = "1",
                name = "Pixel 8 Pro",
                os = "Android 15",
                isCurrentDevice = true,
                isOnline = true,
                lastSyncTimestamp = System.currentTimeMillis(),
            ),
            onUnlink = {},
        )
    }
}
