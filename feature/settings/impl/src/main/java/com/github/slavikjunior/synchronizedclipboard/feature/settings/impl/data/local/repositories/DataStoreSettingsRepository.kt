package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.data.local.repositories

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.UserSettings
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.repository.SettingsRepository
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.data.local.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.annotation.Single

@Single
internal class DataStoreSettingsRepository(
    private val settingsDataStore: SettingsDataStore,
) : SettingsRepository {

    override fun observeSettings(): Flow<UserSettings> = combine(
        settingsDataStore.observeTheme(),
        settingsDataStore.observeKeepHistoryDays(),
        settingsDataStore.observeEmail(),
    ) { theme, keepHistoryDays, email ->
        UserSettings(
            email = email,
            theme = theme,
            keepHistoryDays = keepHistoryDays,
        )
    }

    override suspend fun updateTheme(theme: AppTheme): Result<Unit> = try {
        settingsDataStore.saveTheme(theme)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun logout(): Result<Unit> = try {
        settingsDataStore.saveEmail("")
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
