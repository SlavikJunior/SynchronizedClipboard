package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.core.crypto.CryptoManager
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.AddClipboardItemUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.repository.FakeClipboardRepository
import org.koin.core.annotation.Single

@Single
internal class FakeAddClipboardItemUseCase(
    private val repository: FakeClipboardRepository,
    private val cryptoManager: CryptoManager,
) : AddClipboardItemUseCase {
    override suspend operator fun invoke(item: ClipboardItem) =
        repository.addItem(item.copy(text = cryptoManager.encrypt(item.text)))
}
