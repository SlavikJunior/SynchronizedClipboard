package com.github.slavikjunior.synchronizedclipboard.core.database.di

import android.content.Context
import androidx.room.Room
import com.github.slavikjunior.synchronizedclipboard.core.database.datasource.AppDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/**
 * Koin DI-модуль для слоя данных (Room).
 *
 * `@ComponentScan` заставит Koin Compiler Plugin рекурсивно сканировать
 * пакет `...core.database` на наличие `@Single`/`@Factory` — чтобы новые
 * DAO или Repository можно было добавить одним `@Single`-методом.
 */
@Module
@ComponentScan("com.github.slavikjunior.synchronizedclipboard.core.database")
class DatabaseModule {

    /**
     * Singleton экземпляр Room БД.
     * `Context` резолвится Koin-ом через `androidContext` (настраивается в :app).
     */
    @Single
    fun appDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "syncclip-db"
        )
            .fallbackToDestructiveMigration() // упрощаем миграцию на этапе MVP
            .build()
}
