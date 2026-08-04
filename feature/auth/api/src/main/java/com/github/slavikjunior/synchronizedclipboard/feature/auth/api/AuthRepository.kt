package com.github.slavikjunior.synchronizedclipboard.feature.auth.api

/**
 * Репозиторий аутентификации — абстракция над backend-слое.
 *
 * Реализуется во :feature:auth:impl и инжектируется в UseCase через Koin.
 * API-модуль содержит интерфейс, impl — `internal class`.
 */
interface AuthRepository {

    /**
     * Выполняет вход по [login] и [password].
     * @return [Result.success] — успешный вход, [Result.failure] — ошибка.
     */
    suspend fun signIn(login: String, password: String): Result<Unit>

    /**
     * Выполняет вход через Google (OAuth / One Tap).
     */
    suspend fun signInWithGoogle(): Result<Unit>
}
