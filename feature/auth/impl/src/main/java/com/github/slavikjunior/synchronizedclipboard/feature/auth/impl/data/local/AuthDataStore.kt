package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AuthDataStore private constructor(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    fun observeAccessToken(): kotlinx.coroutines.flow.Flow<String?> =
        kotlinx.coroutines.flow.flow {
            while (true) {
                emit(sharedPreferences.getString(KEY_ACCESS_TOKEN, null))
                kotlinx.coroutines.delay(1000L)
            }
        }

    fun getRefreshToken(): String? = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)

    suspend fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"

        @Volatile
        private var INSTANCE: AuthDataStore? = null

        fun getInstance(context: Context): AuthDataStore =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthDataStore(context).also { INSTANCE = it }
            }
    }
}
