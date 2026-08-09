package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.AddClipboardItemUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRepository
import org.koin.core.annotation.Single

@Single
internal class AddClipboardItemUseCaseImpl(
    private val repository: ClipboardRepository,
) : AddClipboardItemUseCase {
    override suspend operator fun invoke(item: ClipboardItem) = repository.addItem(item)
}
