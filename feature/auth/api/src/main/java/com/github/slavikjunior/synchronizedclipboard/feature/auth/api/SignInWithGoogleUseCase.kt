package com.github.slavikjunior.synchronizedclipboard.feature.auth.api

/**
 * UseCase: вход через Google.
 */
interface SignInWithGoogleUseCase {
    suspend operator fun invoke(): Result<Unit>
}
