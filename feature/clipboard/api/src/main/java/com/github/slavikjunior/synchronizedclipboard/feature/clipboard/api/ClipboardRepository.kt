package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api

import kotlinx.coroutines.flow.Flow

interface ClipboardRepository {
    fun observeClipboard(): Flow<List<ClipboardItem>>
    suspend fun addItem(item: ClipboardItem)
    suspend fun deleteItem(id: String)
    suspend fun pinItem(id: String)
}
