package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.repository.SettingsRepository
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.UpdateThemeUseCase
import org.koin.core.annotation.Single

@Single
internal class UpdateThemeUseCaseImpl(
    private val repository: SettingsRepository,
) : UpdateThemeUseCase {
    override suspend operator fun invoke(theme: AppTheme): Result<Unit> =
        repository.updateTheme(theme)
}
