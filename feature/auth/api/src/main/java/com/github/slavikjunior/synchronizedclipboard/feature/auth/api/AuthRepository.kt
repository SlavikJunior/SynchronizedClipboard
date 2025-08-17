package com.github.slavikjunior.synchronizedclipboard.feature.auth.api

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signIn(login: String, password: String): Result<Unit>

    suspend fun signInWithGoogle(): Result<Unit>

    fun observeAuthState(): Flow<Boolean>

    suspend fun logout()
}
