package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRepository
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.DeleteClipboardItemUseCase
import org.koin.core.annotation.Single

@Single
internal class DeleteClipboardItemUseCaseImpl(
    private val repository: ClipboardRepository,
) : DeleteClipboardItemUseCase {
    override suspend operator fun invoke(id: String) = repository.deleteItem(id)
}
