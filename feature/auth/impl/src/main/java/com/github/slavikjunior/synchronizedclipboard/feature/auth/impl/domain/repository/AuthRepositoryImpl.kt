package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.data

import android.content.Context
import com.github.slavikjunior.synchronizedclipboard.feature.auth.api.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
internal class AuthRepositoryImpl(
    context: Context,
) : AuthRepository {

    private val authDataStore = com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.data.local.AuthDataStore.getInstance(context)

    override suspend fun signIn(login: String, password: String): Result<Unit> = try {
        authDataStore.saveTokens("fake_access_token", "fake_refresh_token")
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signInWithGoogle(): Result<Unit> = try {
        authDataStore.saveTokens("fake_access_token", "fake_refresh_token")
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun observeAuthState(): Flow<Boolean> =
        authDataStore.observeAccessToken().map { !it.isNullOrBlank() }

    override suspend fun logout() {
        authDataStore.clear()
    }
}
