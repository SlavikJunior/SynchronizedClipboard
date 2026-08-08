package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.repository.SettingsRepository
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.UserSettings
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.ObserveSettingsUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
internal class ObserveSettingsUseCaseImpl(
    private val repository: SettingsRepository,
) : ObserveSettingsUseCase {
    override operator fun invoke(): Flow<UserSettings> = repository.observeSettings()
}
