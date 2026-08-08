package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.R
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipEmptyView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipErrorView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipFab
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipLoadingView
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipScaffold
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.components.SyncClipTopAppBar
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.ClipboardEvent
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.ClipboardState

@Composable
internal fun ClipboardScreenContent(
    state: ScreenState<ClipboardState>,
    snackbarHostState: androidx.compose.material3.SnackbarHostState,
    onEvent: (ClipboardEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    SyncClipScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SyncClipTopAppBar(title = "Буфер обмена") },
        floatingActionButton = {
            SyncClipFab(
                onClick = { onEvent(ClipboardEvent.OnFabClicked) },
                contentDescription = "Добавить элемент",
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
                SyncClipLoadingView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            is ScreenState.Success -> {
                val items = state.data.items
                if (items.isEmpty()) {
                    SyncClipEmptyView(
                        message = "Буфер пуст",
                        icon = painterResource(id = R.drawable.ic_clipboard_empty),
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
            }

            is ScreenState.Empty -> {
                SyncClipEmptyView(
                    message = state.message,
                    icon = painterResource(id = R.drawable.ic_clipboard_empty),
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
