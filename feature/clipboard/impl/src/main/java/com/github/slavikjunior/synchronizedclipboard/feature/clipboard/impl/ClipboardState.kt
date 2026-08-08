package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem

internal data class ClipboardState(
    val items: List<ClipboardItem> = emptyList(),
)
