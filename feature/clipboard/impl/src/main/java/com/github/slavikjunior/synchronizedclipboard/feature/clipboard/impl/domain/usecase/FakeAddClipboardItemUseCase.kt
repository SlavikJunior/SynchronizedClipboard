package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.AddClipboardItemUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import org.koin.core.annotation.Single

@Single
internal class FakeAddClipboardItemUseCase(
    private val repository: FakeClipboardRepository,
) : AddClipboardItemUseCase {
    override suspend operator fun invoke(item: ClipboardItem) = repository.addItem(item)
}
