package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRepository
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.PinClipboardItemUseCase
import org.koin.core.annotation.Single

@Single
internal class PinClipboardItemUseCaseImpl(
    private val repository: ClipboardRepository,
) : PinClipboardItemUseCase {
    override suspend operator fun invoke(id: String) = repository.pinItem(id)
}
