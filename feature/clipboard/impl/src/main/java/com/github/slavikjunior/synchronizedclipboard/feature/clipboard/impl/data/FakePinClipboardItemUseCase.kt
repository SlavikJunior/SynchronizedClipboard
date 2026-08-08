package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.PinClipboardItemUseCase
import org.koin.core.annotation.Single

@Single
internal class FakePinClipboardItemUseCase(
    private val repository: FakeClipboardRepository,
) : PinClipboardItemUseCase {
    override suspend operator fun invoke(id: String) = repository.pinItem(id)
}
