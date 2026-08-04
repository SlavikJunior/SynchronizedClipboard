package com.github.slavikjunior.synchronizedclipboard.feature.auth.api

/**
 * UseCase: вход по логину и паролю.
 *
 * Интерфейс в :api (контракт), реализация — `internal` в :impl.
 * Koin резолвит реализацию через `@Single` в [com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.AuthModule].
 */
interface SignInUseCase {
    suspend operator fun invoke(login: String, password: String): Result<Unit>
}
