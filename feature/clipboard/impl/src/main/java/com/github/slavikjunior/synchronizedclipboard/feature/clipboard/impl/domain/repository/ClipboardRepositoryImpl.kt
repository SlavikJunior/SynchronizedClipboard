package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.domain.repository

import com.github.slavikjunior.synchronizedclipboard.core.crypto.CryptoManager
import com.github.slavikjunior.synchronizedclipboard.core.database.dao.ClipboardDao
import com.github.slavikjunior.synchronizedclipboard.core.database.entity.ClipboardItemEntity
import com.github.slavikjunior.synchronizedclipboard.core.cache.ReactiveCache
import com.github.slavikjunior.synchronizedclipboard.core.network.api.NetworkSyncApi
import com.github.slavikjunior.synchronizedclipboard.core.network.model.EncryptedClipboardDto
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRepository
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data.mapper.toDto
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data.mapper.toDomain
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data.mapper.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

/**
 * Репозиторий буфера обмена с паттерном Write-Through Cache + WebSocket E2E-синхронизация.
 *
 * - **Источник истины для UI:** in-memory LRU-кеш ([ReactiveCache]).
 * - **Глухое хранилище:** Room ([ClipboardDao]) без Flow, только suspend-методы.
 * - **Сеть:** [NetworkSyncApi] передаёт ТОЛЬКО зашифрованные данные.
 *
 * При старте кеш "прогревается" из БД. Все мутации сразу попадают в кеш,
 * а запись в БД и отправка в сеть выполняются в фоновых корутинах.
 */
@Single
internal class ClipboardRepositoryImpl(
    private val clipboardDao: ClipboardDao,
    private val cryptoManager: CryptoManager,
    private val networkSyncApi: NetworkSyncApi,
    @Named("io_dispatcher") private val ioDispatcher: CoroutineDispatcher,
    @Named("ClipboardLRU") private val cache: ReactiveCache<String, ClipboardItem>,
) : ClipboardRepository {

    @Volatile
    private var isCacheWarm: Boolean = false

    @Volatile
    private var isListeningToNetwork: Boolean = false

    private val scope = CoroutineScope(ioDispatcher + SupervisorJob())

    override fun observeClipboard(): Flow<List<ClipboardItem>> {
        synchronized(this) {
            if (!isCacheWarm) {
                isCacheWarm = true
                scope.launch {
                    val entities = clipboardDao.getAll()
                    val decrypted = entities.map { it.toDomain().copy(text = cryptoManager.decrypt(it.encryptedText)) }
                    cache.putAll(decrypted.associateBy({ it.id }, { it }))
                }
            }
        }
        startNetworkListening()
        return cache.observeAll()
    }

    override suspend fun addItem(item: ClipboardItem) {
        cache.put(item.id, item)
        scope.launch {
            val encrypted = item.copy(text = cryptoManager.encrypt(item.text))
            val entity = encrypted.toEntity()
            clipboardDao.insert(entity)
            networkSyncApi.sendItem(entity.toDto())
        }
    }

    override suspend fun deleteItem(id: String) {
        cache.remove(id)
        scope.launch {
            clipboardDao.deleteById(id)
        }
    }

    override suspend fun pinItem(id: String) {
        val current = cache.getById(id) ?: return
        val updated = current.copy(isPinned = !current.isPinned)
        cache.put(updated.id, updated)
        scope.launch {
            clipboardDao.insert(updated.toEntity())
        }
    }

    private fun startNetworkListening() {
        synchronized(this) {
            if (isListeningToNetwork) return
            isListeningToNetwork = true
        }
        scope.launch {
            networkSyncApi.observeIncomingItems().collect { dto ->
                val decryptedText = cryptoManager.decrypt(dto.encryptedText)
                val domainItem = ClipboardItem(
                    id = dto.id,
                    text = decryptedText,
                    timestamp = dto.timestamp,
                    sourceDevice = dto.sourceDevice,
                    isPinned = dto.isPinned,
                )
                cache.put(domainItem.id, domainItem)
                clipboardDao.insert(dto.toEntity())
            }
        }
    }

    fun clear() {
        scope.cancel()
    }
}
