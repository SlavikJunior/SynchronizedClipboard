package com.github.slavikjunior.synchronizedclipboard.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.slavikjunior.synchronizedclipboard.core.database.datasource.AppDatabase
import com.github.slavikjunior.synchronizedclipboard.core.database.dao.ClipboardDao
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.github.slavikjunior.synchronizedclipboard.core.database")
class DatabaseModule {

    @Single
    fun appDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "syncclip-db",
        ).addMigrations(MIGRATION_1_2)
        .build()

    @Single
    fun clipboardDao(database: AppDatabase): ClipboardDao = database.clipboardDao()

    private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS dummy_table")
        }
    }
}
