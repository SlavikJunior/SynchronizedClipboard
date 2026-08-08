package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.data.local.repositories

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.UserSettings
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

/**
 * Фейковый репозиторий настроек для MVP.
 *
 * Хранит настройки in-memory в [MutableStateFlow]. В production будет заменён
 * на репозиторий, читающий из DataStore / бэкенда.
 */
@Single
internal class FakeSettingsRepository : SettingsRepository {

    private val _settings = MutableStateFlow(
        UserSettings(
            email = "user@example.com",
            theme = AppTheme.System,
            keepHistoryDays = 7,
        )
    )
    override fun observeSettings(): Flow<UserSettings> = _settings.asStateFlow()

    override suspend fun updateTheme(theme: AppTheme): Result<Unit> = try {
        _settings.value = _settings.value.copy(theme = theme)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun logout(): Result<Unit> = try {
        _settings.value = _settings.value.copy(email = "")
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
