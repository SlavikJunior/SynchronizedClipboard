package com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface ObserveSettingsUseCase {
    operator fun invoke(): Flow<UserSettings>
}
