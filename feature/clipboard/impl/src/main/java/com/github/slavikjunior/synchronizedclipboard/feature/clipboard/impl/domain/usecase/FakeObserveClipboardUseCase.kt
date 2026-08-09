package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.core.crypto.CryptoManager
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ObserveClipboardUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.repository.FakeClipboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
internal class FakeObserveClipboardUseCase(
    private val repository: FakeClipboardRepository,
    private val cryptoManager: CryptoManager,
) : ObserveClipboardUseCase {
    override operator fun invoke(): Flow<List<ClipboardItem>> =
        repository.observeClipboard().map { items: List<ClipboardItem> ->
            items.map { it.copy(text = cryptoManager.decrypt(it.text)) }
        }
}
