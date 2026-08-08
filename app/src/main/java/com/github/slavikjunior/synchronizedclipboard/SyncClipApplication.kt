package com.github.slavikjunior.synchronizedclipboard

import android.app.Application
import com.github.slavikjunior.synchronizedclipboard.core.database.di.DatabaseModule
import com.github.slavikjunior.synchronizedclipboard.core.database.di.module as databaseModule
import com.github.slavikjunior.synchronizedclipboard.core.network.NetworkModule
import com.github.slavikjunior.synchronizedclipboard.core.network.module as networkModule
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.AuthModule
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.module as authModule
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.ClipboardModule
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.module as clipboardModule
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
                AuthModule().authModule(),
                ClipboardModule().clipboardModule(),
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
