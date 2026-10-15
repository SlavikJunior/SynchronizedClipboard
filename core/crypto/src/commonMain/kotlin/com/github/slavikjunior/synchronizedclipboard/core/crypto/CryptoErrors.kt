package com.github.slavikjunior.synchronizedclipboard.core.crypto

/**
 * Строки-заглушки контракта [CryptoManager] — общие для всех платформ,
 * чтобы тесты и реализации опирались на одно значение.
 */
internal object CryptoErrors {
    const val DECRYPTION_FAILED = "🔒 [Ошибка расшифровки]"
}
