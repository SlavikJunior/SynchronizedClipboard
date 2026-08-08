package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.widget.Toast
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.R as DesignR
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.R
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipBottomBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipEmptyView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipErrorView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipFab
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipLoadingView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipScaffold
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipTopAppBar
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.BottomNavTab
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRoute
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DevicesRoute
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.navigation.SettingsRoute
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.event.ClipboardEvent
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.effect.ClipboardEffect
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.model.ClipboardState
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.viewmodel.ClipboardViewModel
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful-обёртка над [ClipboardScreenContent].
 *
 * - `State` собирается через `collectAsStateWithLifecycle`.
 * - `Effect` (one-shot) собирается в `LaunchedEffect`.
 * - ViewModel резолвится через Koin `koinViewModel()`.
 */
@Composable
internal fun ClipboardScreen(
    onNavigateToTab: (com.github.slavikjunior.synchronizedclipboard.core.navigation.Route) -> Unit = {},
    viewModel: ClipboardViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ClipboardEffect.ShowToast -> {
                    Toast.makeText(context, context.getString(effect.messageRes), Toast.LENGTH_SHORT).show()
                }

                is ClipboardEffect.ShowSnackbar -> {
                    val message = context.getString(effect.messageRes)
                    val actionLabel = effect.actionLabelRes?.let { context.getString(it) }
                    val result = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = actionLabel,
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
        onNavigateToTab = onNavigateToTab,
    )
}

@Composable
private fun ClipboardScreenContent(
    state: ScreenState<ClipboardState>,
    snackbarHostState: SnackbarHostState,
    onEvent: (ClipboardEvent) -> Unit,
    onNavigateToTab: (com.github.slavikjunior.synchronizedclipboard.core.navigation.Route) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = false) {
        // На корневом табе ничего не делаем — система сама закроет activity
    }

    SyncClipScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SyncClipTopAppBar(titleRes = R.string.clipboard_title) },
        bottomBar = {
            SyncClipBottomBar(
                currentRoute = ClipboardRoute,
                tabs = listOf(
                    BottomNavTab(
                        route = ClipboardRoute,
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
                    BottomNavTab(
                        route = SettingsRoute,
                        selectedIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                            )
                        },
                        unselectedIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                            )
                        },
                    ),
                ),
                onNavigate = onNavigateToTab,
            )
        },
        floatingActionButton = {
            SyncClipFab(
                onClick = { onEvent(ClipboardEvent.OnFabClicked) },
                contentDescription = stringResource(id = R.string.clipboard_fab_description),
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        when (state) {
            ScreenState.Idle -> Unit
            ScreenState.Loading -> {
                ClipboardLoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            is ScreenState.Success -> {
                val items = state.data.items
                if (items.isEmpty()) {
                ClipboardEmptyContent(
                    message = stringResource(id = R.string.clipboard_empty),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
                } else {
                    ClipboardSuccessContent(
                        items = items,
                        onEvent = onEvent,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                }
            }

            is ScreenState.Empty -> {
                ClipboardEmptyContent(
                    message = state.message,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            is ScreenState.Error -> {
                ClipboardErrorContent(
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
private fun ClipboardLoadingContent(
    modifier: Modifier = Modifier,
) {
    SyncClipLoadingView(modifier = modifier)
}

@Preview(showBackground = true)
@Composable
private fun ClipboardLoadingContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        ClipboardLoadingContent()
    }
}

@Composable
private fun ClipboardErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SyncClipErrorView(
        message = message,
        onRetry = onRetry,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun ClipboardErrorContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        ClipboardErrorContent(message = "Произошла ошибка", onRetry = {})
    }
}

@Composable
private fun ClipboardEmptyContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    SyncClipEmptyView(
        message = message,
        icon = painterResource(id = DesignR.drawable.ic_clipboard_empty),
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun ClipboardEmptyContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        ClipboardEmptyContent(message = "Буфер пуст")
    }
}

@Composable
private fun ClipboardSuccessContent(
    items: List<ClipboardItem>,
    onEvent: (ClipboardEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(
            count = items.size,
            key = { index -> items[index].id },
        ) { index ->
            val item = items[index]
            ClipboardItemCard(
                item = item,
                onCopy = { onEvent(ClipboardEvent.OnItemCopied(item.id)) },
                onDelete = { onEvent(ClipboardEvent.OnItemDeleted(item.id)) },
                onPin = { onEvent(ClipboardEvent.OnItemPinned(item.id)) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClipboardSuccessContentPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        ClipboardSuccessContent(
            items = listOf(
                ClipboardItem(
                    id = "1",
                    text = "Пример текста из буфера обмена",
                    timestamp = System.currentTimeMillis(),
                    sourceDevice = "MacBook Pro",
                    isPinned = true,
                ),
                ClipboardItem(
                    id = "2",
                    text = "Задача: реализовать E2E шифрование",
                    timestamp = System.currentTimeMillis(),
                    sourceDevice = "Pixel 8",
                    isPinned = false,
                ),
            ),
            onEvent = {},
        )
    }
}

@Composable
private fun ClipboardItemCard(
    item: ClipboardItem,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onPin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = null,
                tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))

            androidx.compose.foundation.layout.Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
            ) {
                androidx.compose.material3.Text(
                    text = item.sourceDevice,
                    style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                )

                androidx.compose.material3.Text(
                    text = item.text,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )

                androidx.compose.material3.Text(
                    text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(item.timestamp)),
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))

            androidx.compose.material3.IconButton(onClick = onPin) {
                androidx.compose.material3.Icon(
                    imageVector = if (item.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                    contentDescription = if (item.isPinned) "Открепить" else "Закрепить",
                    tint = if (item.isPinned) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            androidx.compose.material3.IconButton(onClick = onCopy) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Копировать",
                )
            }

            androidx.compose.material3.IconButton(onClick = onDelete) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClipboardItemCardPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        ClipboardItemCard(
            item = ClipboardItem(
                id = "1",
                text = "Пример текста из буфера обмена, который был скопирован на рабочем компьютере.",
                timestamp = System.currentTimeMillis(),
                sourceDevice = "MacBook Pro",
                isPinned = true,
            ),
            onCopy = {},
            onDelete = {},
            onPin = {},
        )
    }
}
