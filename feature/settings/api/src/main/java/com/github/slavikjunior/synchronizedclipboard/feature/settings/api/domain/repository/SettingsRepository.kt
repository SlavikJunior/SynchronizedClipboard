package com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.repository

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<UserSettings>
    suspend fun updateTheme(theme: AppTheme): Result<Unit>
    suspend fun logout(): Result<Unit>
}
