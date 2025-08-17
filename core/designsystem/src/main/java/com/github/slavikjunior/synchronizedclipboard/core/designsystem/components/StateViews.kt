package com.github.slavikjunior.synchronizedclipboard.core.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.R

/**
 * Переиспользуемый loading-view для всех feature-модулей (контракт AGENTS.md).
 * Заполняет весь доступный контейнер и центрирует CircularProgressIndicator.
 */
@Composable
fun SyncClipLoadingView(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onSurface,
            trackColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
@Preview
private fun SyncClipLoadingViewPreview() = SyncClipLoadingView()

/**
 * Переиспользуемый error-view для всех feature-модулей (контракт AGENTS.md).
 * Показывает человеко-читаемое [message] и кнопку «Повторить» с калбэком [onRetry].
 */
@Composable
fun SyncClipErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    @StringRes retryLabelRes: Int = R.string.action_retry,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.padding(top = 16.dp))
        OutlinedButton(onClick = onRetry) {
            Text(text = stringResource(retryLabelRes))
        }
    }
}

@Composable
@Preview
private fun SyncClipErrorViewPreview() = SyncClipErrorView(
    message = "Произошла ошибка",
    onRetry = {},
)

/**
 * Переиспользуемый empty-view для всех feature-модулей (контракт AGENTS.md).
 * Показывает иконку и человеко-читаемое [message].
 *
 * @param iconPainter Painter иконки (всё, что реализует `Painter`: VectorPainter, BitmapPainter).
 */

@Composable
fun SyncClipEmptyView(
    message: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    intentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = intentColor,
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.padding(top = 16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = intentColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
@Preview
private fun SyncClipEmptyViewPreview() = SyncClipEmptyView(
    message = "Нет данных",
    icon = rememberVectorPainter(Icons.Default.Info),
)
