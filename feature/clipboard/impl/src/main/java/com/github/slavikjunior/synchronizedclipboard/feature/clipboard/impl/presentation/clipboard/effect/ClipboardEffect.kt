package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.effect

import androidx.annotation.StringRes

internal sealed interface ClipboardEffect {
    data class ShowSnackbar(
        @StringRes val messageRes: Int,
        @StringRes val actionLabelRes: Int? = null,
        val onAction: (() -> Unit)? = null,
    ) : ClipboardEffect
}
