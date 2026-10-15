package com.github.slavikjunior.synchronizedclipboard.core.crypto

/**
 * Дебривация AES-256 ключа из парольной фразы (PBKDF2-HMAC-SHA256, RFC 8018).
 *
 * Параметры передаются явно, чтобы поведение проверялось тестами
 * с эталонными векторами (RFC 6070).
 */
internal interface PassphraseKeyDeriver {
    fun derive(passphrase: String, salt: String, iterations: Int, outputLengthBytes: Int): ByteArray
}
