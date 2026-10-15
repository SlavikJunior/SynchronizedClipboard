package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.domain.usecase

import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.repository.SettingsRepository
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.domain.usecase.UpdatePassphraseUseCase
import org.koin.core.annotation.Single

@Single
internal class UpdatePassphraseUseCaseImpl(
    private val repository: SettingsRepository,
) : UpdatePassphraseUseCase {
    override suspend operator fun invoke(passphrase: String): Result<Unit> =
        repository.updatePassphrase(passphrase)
}
