package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.R

@Composable
fun ProfileCard(
    email: String,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.settings_profile_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = stringResource(id = R.string.settings_logout),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileCardPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        ProfileCard(email = "user@example.com", onLogout = {})
    }
}
