package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

private val Context.dataStore by preferencesDataStore(name = "settings")

@Single
class SettingsDataStore(private val context: Context) {

    private val themeKey = intPreferencesKey("theme")
    private val keepHistoryDaysKey = intPreferencesKey("keep_history_days")
    private val emailKey = stringPreferencesKey("email")

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = theme.ordinal
        }
    }

    fun observeTheme(): Flow<AppTheme> = context.dataStore.data.map { preferences ->
        val ordinal = preferences[themeKey] ?: AppTheme.System.ordinal
        AppTheme.entries.getOrNull(ordinal) ?: AppTheme.System
    }

    suspend fun saveKeepHistoryDays(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[keepHistoryDaysKey] = days
        }
    }

    fun observeKeepHistoryDays(): Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[keepHistoryDaysKey] ?: 7
    }

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[emailKey] = email
        }
    }

    fun observeEmail(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[emailKey] ?: ""
    }
}
