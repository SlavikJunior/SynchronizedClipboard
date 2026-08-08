package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.data.model

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO для сериализации настроек (сеть / локальное хранилище).
 *
 * Используется только в `:feature:settings:impl`. В `:api` работает
 * чистый [com.github.slavikjunior.synchronizedclipboard.feature.settings.api.UserSettings].
 */
@Serializable
data class UserSettingsData(
    @SerialName("email")
    val email: String,
    @SerialName("theme")
    val theme: AppTheme,
    @SerialName("keep_history_days")
    val keepHistoryDays: Int,
)
