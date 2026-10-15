package com.github.slavikjunior.synchronizedclipboard.core.network.api

import com.github.slavikjunior.synchronizedclipboard.core.network.model.ClipboardType
import com.github.slavikjunior.synchronizedclipboard.core.network.model.EncryptedClipboardDto
import kotlinx.coroutines.flow.Flow

/**
 * API синхронизации буфера обмена с бэкендом.
 *
 * **Отправка**: REST `POST /api/v1/clipboard` — серверный WebSocket
 * элемента от клиента не принимает. **Приём**: WebSocket `/ws/clipboard`,
 * по нотификации `NEW_ITEM` payload докачивается `GET /api/v1/clipboard`.
 *
 * Всё, что уходит по сети, — уже зашифрованные строки (E2E, blind relay).
 */
interface NetworkSyncApi {

    /**
     * Идемпотентная регистрация устройства: если UUID уже сохранён локально
     * — возвращает его, иначе `POST /api/v1/devices` и сохраняет.
     *
     * Имя устройства берётся из платформы (`deviceName()` — `expect`/`actual`),
     * а не передаётся вызывающим кодом.
     */
    suspend fun registerDeviceIfNeeded(): Result<String>

    /**
     * Отправляет зашифрованный элемент. Вызывающий код отвечает за
     * шифрование ([com.github.slavikjunior.synchronizedclipboard.core.crypto.CryptoManager]).
     */
    suspend fun sendItem(payload: String, type: ClipboardType = ClipboardType.TEXT): Result<Unit>

    /**
     * Поток входящих элементов (уже расшифровка — за пределами API).
     * Подписка запускает фоновое WS-подключение с реконнектом.
     */
    fun observeIncomingItems(): Flow<EncryptedClipboardDto>
}
