package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.presentation.settings.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.R

/**
 * Поле ввода парольной фразы для E2E-шифрования.
 *
 * Сохранение — по потере фокуса (а не на каждый символ): декоративный
 * optimistic-флаг [isSaving] блокирует ввод на время записи в DataStore.
 * После успешной записи DataStore эмитит новое значение [passphrase] —
 * локальный текст пересинхронизируется через `remember(passphrase)`.
 *
 * @param passphrase текущая фраза из настроек.
 * @param isSaving true, пока идёт запись в DataStore.
 * @param onPassphraseChanged колбэк с финальным значением (на потере фокуса).
 */
@Composable
fun PassphraseField(
    passphrase: String,
    isSaving: Boolean = false,
    onPassphraseChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember(passphrase) { mutableStateOf(passphrase) }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        enabled = !isSaving,
        label = { Text(text = stringResource(id = R.string.settings_passphrase_label)) },
        supportingText = {
            Text(
                text = stringResource(id = R.string.settings_passphrase_hint),
                style = MaterialTheme.typography.bodySmall,
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && text != passphrase) {
                    onPassphraseChanged(text)
                }
            },
    )
}

@Preview(showBackground = true)
@Composable
private fun PassphraseFieldPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        PassphraseField(passphrase = "correct horse battery staple", onPassphraseChanged = {})
    }
}

@Preview(showBackground = true, name = "Passphrase saving")
@Composable
private fun PassphraseFieldSavingPreview() {
    com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme {
        PassphraseField(passphrase = "", isSaving = true, onPassphraseChanged = {})
    }
}
