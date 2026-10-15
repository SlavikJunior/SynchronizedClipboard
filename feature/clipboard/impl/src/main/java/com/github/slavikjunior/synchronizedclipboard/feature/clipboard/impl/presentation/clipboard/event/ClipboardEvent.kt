package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.event

internal sealed interface ClipboardEvent {
    /**
     * FAB нажат. [text] — содержимое системного буфера обмена, прочитанное
     * UI-слоем (ViewModel не держит `Context`). `null`/blank — буфер пуст.
     */
    data class OnFabClicked(val text: String?) : ClipboardEvent
    data class OnItemCopied(val itemId: String) : ClipboardEvent
    data class OnItemDeleted(val itemId: String) : ClipboardEvent
    data class OnItemPinned(val itemId: String) : ClipboardEvent
    data object Retry : ClipboardEvent
}
