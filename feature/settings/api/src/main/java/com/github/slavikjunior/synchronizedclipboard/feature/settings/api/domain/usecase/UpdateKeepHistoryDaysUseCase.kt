package com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase

interface UpdateKeepHistoryDaysUseCase {

    suspend operator fun invoke(days: Int): Result<Unit>
}