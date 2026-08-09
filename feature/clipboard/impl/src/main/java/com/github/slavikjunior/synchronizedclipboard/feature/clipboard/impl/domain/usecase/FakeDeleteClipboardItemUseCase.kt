package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.DeleteClipboardItemUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.repository.FakeClipboardRepository
import org.koin.core.annotation.Single

@Single
internal class FakeDeleteClipboardItemUseCase(
    private val repository: FakeClipboardRepository,
) : DeleteClipboardItemUseCase {
    override suspend operator fun invoke(id: String) = repository.deleteItem(id)
}
