package com.github.slavikjunior.synchronizedclipboard.core.crypto

/**
 * Источник пользовательской парольной фразы.
 *
 * Возвращает `null` или пустую строку, пока фраза не задана — в этом случае
 * [CryptoManager] работает на эфемерном (локальном) ключе: данные шифруются,
 * но остаются device-local. Это переходное поведение до того, как пользователь
 * задаст общую фразу в настройках.
 *
 * Реализации зависят от клиента:
 *  - Android — DataStore-backed провайдер из `:feature:settings:impl`;
 *  - desktop CLI — провайдер из конфигурационного файла.
 */
fun interface PassphraseProvider {
    fun currentPassphrase(): String?
}
