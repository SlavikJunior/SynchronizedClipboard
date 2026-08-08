package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
internal class FakeClipboardRepository : ClipboardRepository {

    private val _items = MutableStateFlow(
        listOf(
            ClipboardItem(
                id = "1",
                text = "Пример текста из буфера обмена, который был скопирован на рабочем компьютере.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                sourceDevice = "MacBook Pro",
                isPinned = true,
            ),
            ClipboardItem(
                id = "2",
                text = "https://github.com/slavikjunior/synchronized-clipboard",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                sourceDevice = "iPhone 15",
                isPinned = false,
            ),
            ClipboardItem(
                id = "3",
                text = "Задача: реализовать end-to-end шифрование для синхронизации буфера обмена между устройствами. Необходимо использовать RSA + AES ключи.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                sourceDevice = "Pixel 8",
                isPinned = false,
            ),
            ClipboardItem(
                id = "4",
                text = "Meeting notes: sync protocol v2, latency target < 200ms",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
                sourceDevice = "MacBook Pro",
                isPinned = false,
            ),
        )
    )

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
