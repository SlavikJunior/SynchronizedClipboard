package com.github.slavikjunior.synchronizedclipboard.core.crypto

/**
 * Менеджер шифрования для безопасного хранения данных буфера обмена.
 *
 * AES-256-GCM. Ключ **деривируется из пользовательской парольной фразы**
 * (PBKDF2-HMAC-SHA256, см. [PassphraseKdf]) — это делает шифротексты
 * совместимыми между устройствами пользователя (E2E): Mac, Android и
 * desktop-клиенты получают идентичный ключ из одной фразы.
 *
 * Реализации:
 *  - Android и JVM (desktop) — JCA `javax.crypto` (`JcaCryptoManager`);
 *  - iOS — CryptoKit через `dev.whyoleg.cryptography` (`WhyolegCryptoManager`).
 *
 * Пока парольная фраза не задана ([PassphraseProvider] возвращает `null`),
 * менеджер работает на эфемерном ключе: данные шифруются, но остаются
 * device-local. Кеш ключа инвалидируется при смене фразы.
 *
 * Формат выходной строки одинаков на всех платформах — см. [CipherTextCodec].
 */
interface CryptoManager {

    /**
     * Шифрует [plainText] и возвращает строку в формате `Base64(IV):Base64(cipherText)`.
     */
    fun encrypt(plainText: String): String

    /**
     * Расшифровывает строку формата `Base64(IV):Base64(cipherText)`.
     * При ошибке возвращает [CryptoErrors.DECRYPTION_FAILED].
     */
    fun decrypt(cipherText: String): String
}
