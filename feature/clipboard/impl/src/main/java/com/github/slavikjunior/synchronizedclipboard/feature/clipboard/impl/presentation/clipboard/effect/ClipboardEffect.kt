package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.effect

internal sealed interface ClipboardEffect {
    data class ShowToast(val message: String) : ClipboardEffect
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val onAction: (() -> Unit)? = null,
    ) : ClipboardEffect
}
