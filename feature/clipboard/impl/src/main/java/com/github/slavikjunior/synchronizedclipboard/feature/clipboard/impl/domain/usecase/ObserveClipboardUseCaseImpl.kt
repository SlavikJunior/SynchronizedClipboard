package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRepository
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ObserveClipboardUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
internal class ObserveClipboardUseCaseImpl(
    private val repository: ClipboardRepository,
) : ObserveClipboardUseCase {
    override operator fun invoke(): Flow<List<ClipboardItem>> = repository.observeClipboard()
}
