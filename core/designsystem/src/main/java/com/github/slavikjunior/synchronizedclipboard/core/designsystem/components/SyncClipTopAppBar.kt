package com.github.slavikjunior.synchronizedclipboard.core.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncClipTopAppBar(
    @StringRes titleRes: Int,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    colors: TopAppBarColors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(),
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = titleRes)) },
        modifier = modifier,
        colors = colors,
    )
}
