package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.data

import com.github.slavikjunior.synchronizedclipboard.feature.auth.api.AuthRepository
import com.github.slavikjunior.synchronizedclipboard.feature.auth.api.SignInWithGoogleUseCase
import org.koin.core.annotation.Single

/**
 * Реализация [SignInWithGoogleUseCase] — делегирует работу [AuthRepository].
 *
 * `@Single` — singleton, auto-binds к [SignInWithGoogleUseCase].
 * `internal` — не покидает :feature:auth:impl.
 */
@Single
internal class SignInWithGoogleUseCaseImpl(
    private val repository: AuthRepository,
) : SignInWithGoogleUseCase {

    override suspend fun invoke(): Result<Unit> =
        repository.signInWithGoogle()
}
