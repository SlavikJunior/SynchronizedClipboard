package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl

import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.data.local.repositories.FakeSettingsRepository
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.domain.usecase.LogoutUseCaseImpl
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.domain.usecase.ObserveSettingsUseCaseImpl
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.domain.usecase.UpdateThemeUseCaseImpl
import org.koin.core.annotation.Module
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Single

/**
 * Koin-модуль фичи Settings.
 *
 * `@ComponentScan` сканирует весь пакет `...feature.settings.impl` и
 * автоматически регистрирует все `@Single`/`@Factory`-классы
 * (FakeSettingsRepository, UseCase, ViewModel).
 */
@Module
@ComponentScan("com.github.slavikjunior.synchronizedclipboard.feature.settings.impl")
class SettingsModule
