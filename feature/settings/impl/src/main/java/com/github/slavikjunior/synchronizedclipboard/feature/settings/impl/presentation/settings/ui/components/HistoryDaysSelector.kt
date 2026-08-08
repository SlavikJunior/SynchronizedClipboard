package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.R

private val HISTORY_DAYS_OPTIONS = listOf(7, 14, 30, 90)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDaysSelector(
    keepHistoryDays: Int,
    onHistoryDaysChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = R.string.settings_history_title),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1f),
        ) {
            OutlinedTextField(
                value = stringResource(id = R.string.settings_history_days, keepHistoryDays),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                HISTORY_DAYS_OPTIONS.forEach { days ->
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.settings_history_days, days)) },
                        onClick = {
                            onHistoryDaysChanged(days)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryDaysSelectorPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        HistoryDaysSelector(keepHistoryDays = 7, onHistoryDaysChanged = {})
    }
}
