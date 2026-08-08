package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.data

import com.github.slavikjunior.synchronizedclipboard.feature.auth.api.AuthRepository
import org.koin.core.annotation.Single

/**
 * Стаб-реализация [AuthRepository] для MVP.
 *
 * E2E-шифрование и реальный Ktor-бэкенд добавятся позже.
 * Сейчас sign-in всегда успешен — чтобы протестировать MVI + навигацию.
 *
 * `@Single` — singleton в Koin graph, auto-binds к [AuthRepository] (интерфейсу).
 * `internal` — реализация не покидает :feature:auth:impl.
 */
@Single
internal class AuthRepositoryImpl : AuthRepository {

    override suspend fun signIn(login: String, password: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun signInWithGoogle(): Result<Unit> =
        Result.success(Unit)
}
