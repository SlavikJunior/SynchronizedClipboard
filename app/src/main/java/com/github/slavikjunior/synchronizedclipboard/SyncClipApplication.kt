package com.github.slavikjunior.synchronizedclipboard

import android.app.Application
import com.github.slavikjunior.synchronizedclipboard.core.database.di.DatabaseModule
import com.github.slavikjunior.synchronizedclipboard.core.database.di.module as databaseModule
import com.github.slavikjunior.synchronizedclipboard.core.crypto.di.CryptoModule
import com.github.slavikjunior.synchronizedclipboard.core.crypto.di.module as cryptoModule
import com.github.slavikjunior.synchronizedclipboard.core.di.DiModule
import com.github.slavikjunior.synchronizedclipboard.core.di.module as diModule
import com.github.slavikjunior.synchronizedclipboard.core.cache.di.CacheModule
import com.github.slavikjunior.synchronizedclipboard.core.cache.di.module as cacheModule
import com.github.slavikjunior.synchronizedclipboard.core.network.NetworkModule
import com.github.slavikjunior.synchronizedclipboard.core.network.module as networkModule
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.AuthModule
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.module as authModule
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.ClipboardModule
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.module as clipboardModule
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.DevicesModule
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.module as devicesModule
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.SettingsModule
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.module as settingsModule
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class SyncClipApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@SyncClipApplication)
            modules(
                jsonModule,
                NetworkModule().networkModule(),
                DatabaseModule().databaseModule(),
                CryptoModule().cryptoModule(),
                DiModule().diModule(),
                CacheModule().cacheModule(),
                AuthModule().authModule(),
                ClipboardModule().clipboardModule(),
                DevicesModule().devicesModule(),
                SettingsModule().settingsModule(),
            )
        }
    }
}

private val jsonModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }
    }
}
