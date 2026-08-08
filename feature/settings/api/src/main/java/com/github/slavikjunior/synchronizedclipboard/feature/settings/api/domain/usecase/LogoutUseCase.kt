package com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase

interface LogoutUseCase {
    suspend operator fun invoke(): Result<Unit>
}
