package com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme

interface UpdateThemeUseCase {
    suspend operator fun invoke(theme: AppTheme): Result<Unit>
}
