package com.github.slavikjunior.synchronizedclipboard.core.crypto

/**
 * Менеджер шифрования для безопасного хранения данных буфера обмена.
 *
 * AES-256-GCM. Источник ключа платформенный:
 *  - Android — AndroidKeyStore (`javax.crypto` + JCA);
 *  - iOS — CryptoKit через `dev.whyoleg.cryptography` (переходное состояние,
 *    ключ в памяти процесса).
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
     * При ошибке возвращает `"🔒 [Ошибка расшифровки]"`.
     */
    fun decrypt(cipherText: String): String
}
