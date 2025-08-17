package com.github.slavikjunior.synchronizedclipboard.core.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.LinkedHashMap

/**
 * LRU-реализация [ReactiveCache] на базе [LinkedHashMap].
 *
 * При превышении [maxSize] автоматически удаляется самый старый (least recently used) элемент.
 * Все изменения атомарно отражаются в реактивном потоке [observeAll].
 */
class LruReactiveCache<K, V>(
    private val maxSize: Int,
) : ReactiveCache<K, V> {

    private val cache = object : LinkedHashMap<K, V>(maxSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
            return size > maxSize
        }
    }

    private val cacheState = MutableStateFlow<List<V>>(emptyList())

    override fun observeAll(): Flow<List<V>> = cacheState.asStateFlow()

    override fun getById(key: K): V? = synchronized(cache) { cache[key] }

    override suspend fun put(key: K, value: V) {
        synchronized(cache) {
            cache[key] = value
            cacheState.value = cache.values.toList()
        }
    }

    override suspend fun remove(key: K) {
        synchronized(cache) {
            cache.remove(key)
            cacheState.value = cache.values.toList()
        }
    }

    override suspend fun putAll(items: Map<K, V>) {
        synchronized(cache) {
            items.forEach { (key, value) ->
                cache[key] = value
            }
            cacheState.value = cache.values.toList()
        }
    }

    override suspend fun clear() {
        synchronized(cache) {
            cache.clear()
            cacheState.value = emptyList()
        }
    }
}
