package com.github.slavikjunior.synchronizedclipboard.core.database.datasource

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.slavikjunior.synchronizedclipboard.core.database.di.DATABASE_NAME
import com.github.slavikjunior.synchronizedclipboard.core.database.di.MIGRATION_1_2
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Доступ к `android.content.Context` вне Koin-графа.
 *
 * `Context` регистрируется в `:app` (`androidContext(...)`), поэтому используется
 * ленивое `inject()`: фабрика вызывается уже после `startKoin`.
 *
 * `ioDispatcher` сюда намеренно **не** заводится — он приходит параметром в
 * [databaseBuilder] из Koin-модуля (см. `DatabaseModule`).
 */
private object AndroidContextHolder : KoinComponent {
    val context: Context by inject()
}

/**
 * Android-реализация: БД открывается через `SupportSQLiteOpenHelper` поверх
 * системного SQLite. Экземпляр `AppDatabase_Impl` Room подставляет сам по
 * сгенерированному `actual object AppDatabaseConstructor`.
 */
actual fun databaseBuilder(
    ioDispatcher: CoroutineDispatcher,
): RoomDatabase.Builder<AppDatabase> =
    Room.databaseBuilder(
        context = AndroidContextHolder.context,
        klass = AppDatabase::class.java,
        name = DATABASE_NAME,
    )
        .setQueryCoroutineContext(ioDispatcher)
        .addMigrations(MIGRATION_1_2)
