package com.github.slavikjunior.synchronizedclipboard.core.cache

import kotlinx.coroutines.flow.Flow

/**
 * Реактивный кеш с поддержкой наблюдения за всем набором значений.
 *
 * @param K тип ключа
 * @param V тип значения
 */
interface ReactiveCache<K, V> {

    /**
     * Реактивный поток всего текущего содержимого кеша.
     */
    fun observeAll(): Flow<List<V>>

    /**
     * Возвращает значение по ключу или null, если ключ отсутствует.
     */
    fun getById(key: K): V?

    /**
     * Добавляет или обновляет значение по ключу.
     */
    suspend fun put(key: K, value: V)

    /**
     * Удаляет значение по ключу.
     */
    suspend fun remove(key: K)

    /**
     * Массовое добавление/обновление значений.
     */
    suspend fun putAll(items: Map<K, V>)

    /**
     * Очищает кеш полностью.
     */
    suspend fun clear()
}
