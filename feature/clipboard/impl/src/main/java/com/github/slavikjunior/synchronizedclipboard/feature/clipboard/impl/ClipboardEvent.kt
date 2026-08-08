package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl

internal sealed interface ClipboardEvent {
    data object OnFabClicked : ClipboardEvent
    data class OnItemCopied(val itemId: String) : ClipboardEvent
    data class OnItemDeleted(val itemId: String) : ClipboardEvent
    data class OnItemPinned(val itemId: String) : ClipboardEvent
}
