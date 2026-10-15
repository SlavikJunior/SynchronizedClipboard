package com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase

interface UpdatePassphraseUseCase {
    suspend operator fun invoke(passphrase: String): Result<Unit>
}
