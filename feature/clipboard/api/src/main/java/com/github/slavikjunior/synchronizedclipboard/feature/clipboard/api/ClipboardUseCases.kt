package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api

import kotlinx.coroutines.flow.Flow

interface ObserveClipboardUseCase {
    operator fun invoke(): Flow<List<ClipboardItem>>
}

interface AddClipboardItemUseCase {
    suspend operator fun invoke(item: ClipboardItem)
}

interface DeleteClipboardItemUseCase {
    suspend operator fun invoke(id: String)
}

interface PinClipboardItemUseCase {
    suspend operator fun invoke(id: String)
}
