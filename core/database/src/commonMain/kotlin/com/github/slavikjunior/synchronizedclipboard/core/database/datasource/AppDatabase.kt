package com.github.slavikjunior.synchronizedclipboard.core.database.datasource

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.github.slavikjunior.synchronizedclipboard.core.database.dao.ClipboardDao
import com.github.slavikjunior.synchronizedclipboard.core.database.entity.ClipboardItemEntity
import kotlinx.coroutines.CoroutineDispatcher

/**
 * База данных приложения.
 *
 * `@ConstructedBy` обязателен для Room в Kotlin Multiplatform: он указывает на
 * [AppDatabaseConstructor], `actual`-реализацию которого **генерирует сам
 * Room-компилятор** — вручную `actual object` писать нельзя, Room KSP начинает
 * требовать `expect`-объявление на стороне платформы и падает.
 */
@ConstructedBy(AppDatabaseConstructor::class)
@Database(
    entities = [ClipboardItemEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clipboardDao(): ClipboardDao
}

/**
 * Фабрика [AppDatabase], `actual`-реализацию которой генерирует Room-компилятор.
 *
 * Важно: сгенерированная реализация сводится к `AppDatabase_Impl()`, то есть
 * обходит билдер и не применяет ни имя файла БД, ни миграции, ни драйвер.
 * Поэтому для реального создания БД используется [createAppDatabase] с
 * platform-specific билдером, а объявление нужно Room-у ради `@ConstructedBy`.
 */
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

/**
 * Platform-specific билдер RoomDatabase.
 *
 * Room намеренно не имеет общего билдера: `RoomDatabase.Builder` реализован
 * через `expect/actual` и на каждой платформе устроен по-разному —
 *
 *  - **Android**: `Room.databaseBuilder(context, klass, name)` поверх
 *    системного SQLite;
 *  - **iOS**: `Room.databaseBuilder<AppDatabase>(name)` с `BundledSQLiteDriver`.
 *
 * [ioDispatcher] прокидывается из Koin-модуля (binding `@Named("io_dispatcher")`
 * из `:core:di`) и применяется через `setQueryCoroutineContext`: Room выполняет
 * корутины запросов на нём, и на Android, и на iOS симметрично. Резолв диспатчера
 * идёт через параметр `@Single`-метода, а не через `KoinComponent` внутри билдера —
 * иначе Koin compile-safety в `:app` не видит определение из зависимого модуля
 * и падает с `KOIN-D003`.
 */
expect fun databaseBuilder(
    ioDispatcher: CoroutineDispatcher,
): RoomDatabase.Builder<AppDatabase>

/**
 * Создаёт экземпляр БД из platform-specific билдера.
 */
fun createAppDatabase(ioDispatcher: CoroutineDispatcher): AppDatabase =
    databaseBuilder(ioDispatcher).build()