package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.repository.SettingsRepository
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.UpdateKeepHistoryDaysUseCase
import org.koin.core.annotation.Single

@Single
internal class UpdateKeepHistoryDaysUseCaseImpl(
    private val repository: SettingsRepository
) : UpdateKeepHistoryDaysUseCase {

    override suspend fun invoke(days: Int): Result<Unit> {
        check(days >= 0) { "Days must be a non-negative integer" }
        return repository.updateKeepHistoryDays(days)
    }
}
