package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.repository

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
internal class FakeClipboardRepository : ClipboardRepository {

    private val _items = MutableStateFlow<List<ClipboardItem>>(emptyList())

    override fun observeClipboard(): Flow<List<ClipboardItem>> = _items.asStateFlow()

    override suspend fun addItem(item: ClipboardItem) {
        _items.value = listOf(item) + _items.value
    }

    override suspend fun deleteItem(id: String) {
        _items.value = _items.value.filterNot { it.id == id }
    }

    override suspend fun pinItem(id: String) {
        _items.value = _items.value.map { item ->
            if (item.id == id) item.copy(isPinned = !item.isPinned) else item
        }
    }
}