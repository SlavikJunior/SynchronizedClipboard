package com.github.slavikjunior.synchronizedclipboard.core.crypto

/**
 * Менеджер шифрования для безопасного хранения данных буфера обмена.
 *
 * Использует AES-256-GCM с ключом из AndroidKeyStore.
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
