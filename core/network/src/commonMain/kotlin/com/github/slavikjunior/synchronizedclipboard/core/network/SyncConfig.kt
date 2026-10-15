package com.github.slavikjunior.synchronizedclipboard.core.network

/**
 * Рантайм-конфигурация подключения к бэкенду.
 *
 * Значения приходят из [SyncConfigProvider] — настраиваемый хост
 * (settings-экран на Android, флаги CLI на desktop), а не хардкод.
 *
 * @property secure true → https/wss, false → http/ws.
 */
data class SyncConfig(
    val host: String,
    val port: Int,
    val secure: Boolean = false,
) {
    val webSocketScheme: String get() = if (secure) "wss" else "ws"
    val httpScheme: String get() = if (secure) "https" else "http"

    companion object {
        val DEFAULT = SyncConfig(host = "localhost", port = 8080, secure = false)
    }
}

/**
 * Источник текущего [SyncConfig]. Меняется в рантайме (пользователь вводит
 * новый адрес сервера) — KtorNetworkSyncApi читает конфиг перед каждой
 * операцией, чтобы переподключение шло сразу на новый адрес.
 */
fun interface SyncConfigProvider {
    fun current(): SyncConfig
}
