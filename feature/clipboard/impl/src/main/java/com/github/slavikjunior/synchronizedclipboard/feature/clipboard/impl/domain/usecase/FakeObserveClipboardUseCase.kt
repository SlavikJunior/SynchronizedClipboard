package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ObserveClipboardUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
internal class FakeObserveClipboardUseCase(
    private val repository: FakeClipboardRepository,
) : ObserveClipboardUseCase {
    override operator fun invoke(): Flow<List<ClipboardItem>> = repository.observeClipboard()
}
