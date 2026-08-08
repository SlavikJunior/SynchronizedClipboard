package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.data

import com.github.slavikjunior.synchronizedclipboard.feature.auth.api.AuthRepository
import com.github.slavikjunior.synchronizedclipboard.feature.auth.api.SignInUseCase
import org.koin.core.annotation.Single

/**
 * Реализация [SignInUseCase] — делегирует работу [AuthRepository].
 *
 * `@Single` — singleton, auto-binds к [SignInUseCase].
 * `internal` — не покидает :feature:auth:impl.
 */
@Single
internal class SignInUseCaseImpl(
    private val repository: AuthRepository,
) : SignInUseCase {

    override suspend fun invoke(login: String, password: String): Result<Unit> =
        repository.signIn(login, password)
}
