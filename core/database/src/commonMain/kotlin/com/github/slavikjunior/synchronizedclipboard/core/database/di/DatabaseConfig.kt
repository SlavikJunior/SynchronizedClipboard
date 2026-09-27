package com.github.slavikjunior.synchronizedclipboard.core.database.di

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * Имя файла БД. Одинаково для Android и iOS — схема SQLite общая.
 */
internal const val DATABASE_NAME = "syncclip-db"

/**
 * Миграция 1 → 2: удаление заглушки `dummy_table`, оставшейся от скаффолдинга.
 *
 * `fallbackToDestructiveMigration()` намеренно НЕ используется — потеря истории
 * буфера обмена недопустима.
 *
 * Используем KMP-совместимый API: `SQLiteConnection` вместо `SupportSQLiteDatabase`.
 */
internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS dummy_table")
    }
}