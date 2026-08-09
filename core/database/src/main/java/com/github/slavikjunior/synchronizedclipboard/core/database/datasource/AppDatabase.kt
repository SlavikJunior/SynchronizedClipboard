package com.github.slavikjunior.synchronizedclipboard.core.database.datasource

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.slavikjunior.synchronizedclipboard.core.database.entity.ClipboardItemEntity

@Entity(tableName = "dummy_table")
data class DummyEntity(
    @PrimaryKey val id: Int = 1
)

@Database(
    entities = [DummyEntity::class, ClipboardItemEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters
abstract class AppDatabase : RoomDatabase() {

    abstract fun clipboardDao(): com.github.slavikjunior.synchronizedclipboard.core.database.dao.ClipboardDao
}
