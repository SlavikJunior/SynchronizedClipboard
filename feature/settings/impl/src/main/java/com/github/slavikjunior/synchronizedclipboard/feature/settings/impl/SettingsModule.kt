package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl

import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.data.local.SettingsDataStore
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.data.local.repositories.DataStoreSettingsRepository
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.domain.usecase.LogoutUseCaseImpl
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.domain.usecase.ObserveSettingsUseCaseImpl
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.domain.usecase.UpdateThemeUseCaseImpl
import org.koin.core.annotation.Module
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.github.slavikjunior.synchronizedclipboard.feature.settings.impl")
class SettingsModule
