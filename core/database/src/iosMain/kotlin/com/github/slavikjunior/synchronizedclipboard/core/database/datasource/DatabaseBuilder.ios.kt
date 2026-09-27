package com.github.slavikjunior.synchronizedclipboard.core.database.datasource

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.slavikjunior.synchronizedclipboard.core.database.di.DATABASE_NAME
import com.github.slavikjunior.synchronizedclipboard.core.database.di.MIGRATION_1_2
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineDispatcher
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

/**
 * iOS-реализация: БД открывается через `BundledSQLiteDriver` поверх
 * самодостаточного SQLite. Экземпляр `AppDatabase_Impl` Room подставляет сам
 * по сгенерированному `actual object AppDatabaseConstructor`.
 *
 * [ioDispatcher] прокидывается из Koin-модуля (binding `@Named("io_dispatcher")`)
 * и применяется через `setQueryCoroutineContext` — симметрично Android.
 */
actual fun databaseBuilder(
    ioDispatcher: CoroutineDispatcher,
): RoomDatabase.Builder<AppDatabase> =
    Room.databaseBuilder<AppDatabase>(name = DATABASE_NAME)
        .setDriver(
            BundledSQLiteDriver().apply {
                open(fileName = resolveDatabasePath())
            },
        )
        .setQueryCoroutineContext(ioDispatcher)
        .addMigrations(MIGRATION_1_2)

@OptIn(ExperimentalForeignApi::class)
private fun resolveDatabasePath(): String {
    val applicationSupportUrl: NSURL = requireNotNull(
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSApplicationSupportDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        ),
    ) {
        "Не удалось получить директорию Application Support для БД"
    }

    val supportPath = requireNotNull(applicationSupportUrl.path) {
        "Путь Application Support недоступен"
    }

    return "$supportPath/$DATABASE_NAME"
}
