package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.repository.SettingsRepository
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.LogoutUseCase
import org.koin.core.annotation.Single

@Single
internal class LogoutUseCaseImpl(
    private val repository: SettingsRepository,
) : LogoutUseCase {
    override suspend operator fun invoke(): Result<Unit> = repository.logout()
}
