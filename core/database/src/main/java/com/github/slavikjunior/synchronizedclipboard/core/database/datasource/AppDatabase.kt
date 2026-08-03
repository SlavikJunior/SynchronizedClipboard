package com.github.slavikjunior.synchronizedclipboard.core.database.datasource

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

/**
 * Заглушка-сущность, так как Room требует наличие хотя бы одной @Entity
 * для генерации кода базы данных.
 */
@Entity(tableName = "dummy_table")
data class DummyEntity(
    @PrimaryKey val id: Int = 1
)

/**
 * База данных приложения SynchronizedClipboard.
 *
 * Feature-модули будут добавлять `@Entity` и `@Dao` через наследование или
 * отдельные пакеты. Room сгенерирует skeleton-код через `room-compiler` (KSP).
 */
@Database(
    entities = [DummyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase()
