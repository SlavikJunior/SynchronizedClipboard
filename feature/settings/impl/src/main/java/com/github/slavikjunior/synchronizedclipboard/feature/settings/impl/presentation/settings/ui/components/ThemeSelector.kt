package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.R

@Composable
fun ThemeSelector(
    theme: AppTheme,
    onThemeChanged: (AppTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = theme == AppTheme.Light,
            onClick = { onThemeChanged(AppTheme.Light) },
            label = { Text(text = stringResource(id = R.string.settings_theme_light)) },
            modifier = Modifier.weight(1f),
        )
        FilterChip(
            selected = theme == AppTheme.Dark,
            onClick = { onThemeChanged(AppTheme.Dark) },
            label = { Text(text = stringResource(id = R.string.settings_theme_dark)) },
            modifier = Modifier.weight(1f),
        )
        FilterChip(
            selected = theme == AppTheme.System,
            onClick = { onThemeChanged(AppTheme.System) },
            label = { Text(text = stringResource(id = R.string.settings_theme_system)) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemeSelectorPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        ThemeSelector(theme = AppTheme.System, onThemeChanged = {})
    }
}
