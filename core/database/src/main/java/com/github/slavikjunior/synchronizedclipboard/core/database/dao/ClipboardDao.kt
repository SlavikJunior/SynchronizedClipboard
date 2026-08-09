package com.github.slavikjunior.synchronizedclipboard.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.slavikjunior.synchronizedclipboard.core.database.entity.ClipboardItemEntity

@Dao
interface ClipboardDao {

    @Query("SELECT * FROM clipboard_items ORDER BY timestamp DESC")
    suspend fun getAll(): List<ClipboardItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ClipboardItemEntity)

    @Query("DELETE FROM clipboard_items WHERE id = :id")
    suspend fun deleteById(id: String)
}
