package com.github.slavikjunior.synchronizedclipboard.core.network.api

import com.github.slavikjunior.synchronizedclipboard.core.network.model.EncryptedClipboardDto
import kotlinx.coroutines.flow.Flow

/**
 * API для синхронизации буфера обмена через WebSocket.
 *
 * Вся передаваемая по сети информация — уже зашифрованная ([EncryptedClipboardDto]).
 * Расшифровка и сохранение в локальный кеш/базу выполняет [ClipboardRepositoryImpl].
 */
interface NetworkSyncApi {

    /**
     * Поток входящих зашифрованных элементов от сервера.
     */
    suspend fun observeIncomingItems(): Flow<EncryptedClipboardDto>

    /**
     * Отправляет зашифрованный элемент на сервер.
     */
    suspend fun sendItem(item: EncryptedClipboardDto)
}
