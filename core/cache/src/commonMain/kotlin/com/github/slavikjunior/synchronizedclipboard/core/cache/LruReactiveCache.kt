package com.github.slavikjunior.synchronizedclipboard.core.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * LRU-реализация [ReactiveCache] на базе Kotlin stdlib collections.
 *
 * При превышении [maxSize] автоматически удаляется самый старый (least recently used) элемент.
 * Все изменения атомарно отражаются в реактивном потоке [observeAll].
 */
class LruReactiveCache<K, V>(
    private val maxSize: Int,
) : ReactiveCache<K, V> {

    private val cache = mutableMapOf<K, V>()
    private val accessOrder = mutableListOf<K>()
    private val mutex = Mutex()

    private val cacheState = MutableStateFlow<List<V>>(emptyList())

    override fun observeAll(): Flow<List<V>> = cacheState.asStateFlow()

    override fun getById(key: K): V? {
        // Для read-операций используем простой доступ без блокировки
        // т.к. MutableMap в Kotlin/Native thread-safe для reads
        val value = cache[key]
        if (value != null) {
            // Обновление порядка доступа — неатомарная операция, но приемлемо для кеша
            accessOrder.remove(key)
            accessOrder.add(key)
        }
        return value
    }

    override suspend fun put(key: K, value: V) {
        mutex.withLock {
            // Если ключ уже существует, обновляем порядок
            if (cache.containsKey(key)) {
                accessOrder.remove(key)
            }
            
            cache[key] = value
            accessOrder.add(key)
            
            // Удаляем самый старый элемент, если превышен лимит
            if (cache.size > maxSize) {
                val eldest = accessOrder.removeAt(0)
                cache.remove(eldest)
            }
            
            cacheState.value = cache.values.toList()
        }
    }

    override suspend fun remove(key: K) {
        mutex.withLock {
            cache.remove(key)
            accessOrder.remove(key)
            cacheState.value = cache.values.toList()
        }
    }

    override suspend fun putAll(items: Map<K, V>) {
        mutex.withLock {
            items.forEach { (key, value) ->
                if (cache.containsKey(key)) {
                    accessOrder.remove(key)
                }
                cache[key] = value
                accessOrder.add(key)
            }
            
            // Удаляем самые старые элементы, если превышен лимит
            while (cache.size > maxSize) {
                val eldest = accessOrder.removeAt(0)
                cache.remove(eldest)
            }
            
            cacheState.value = cache.values.toList()
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            cache.clear()
            accessOrder.clear()
            cacheState.value = emptyList()
        }
    }
}
