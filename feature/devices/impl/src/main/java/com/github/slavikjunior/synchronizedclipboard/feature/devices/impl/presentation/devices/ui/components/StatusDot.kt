package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme

@Composable
fun StatusDot(
    isOnline: Boolean,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
) {
    val color = if (isOnline) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Canvas(
        modifier = modifier.size(8.dp),
    ) {
        drawCircle(color = color)
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusDotPreview() {
    SyncClipTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusDot(isOnline = true)
            StatusDot(isOnline = false)
        }
    }
}
