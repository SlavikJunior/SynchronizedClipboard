package com.github.slavikjunior.synchronizedclipboard.core.database.di

import com.github.slavikjunior.synchronizedclipboard.core.database.dao.ClipboardDao
import com.github.slavikjunior.synchronizedclipboard.core.database.datasource.AppDatabase
import com.github.slavikjunior.synchronizedclipboard.core.database.datasource.createAppDatabase
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

/**
 * Koin-модуль базы данных.
 *
 * Биндинги объявлены методами класса: Koin Compiler Plugin связывает определения
 * с `@Module`-классом, и только в такой форме compile-time валидация графа
 * видит бинды из зависимых модулей.
 *
 * `ioDispatcher` пробрасывается параметром из `:core:di` — определение
 * с квалификатором `io_dispatcher` живёт там и резолвится Koin-ом при старте.
 */
@Module
@ComponentScan("com.github.slavikjunior.synchronizedclipboard.core.database")
class DatabaseModule {

    @Single
    fun appDatabase(
        @Named("io_dispatcher") ioDispatcher: CoroutineDispatcher,
    ): AppDatabase = createAppDatabase(ioDispatcher)

    @Single
    fun clipboardDao(database: AppDatabase): ClipboardDao = database.clipboardDao()
}
