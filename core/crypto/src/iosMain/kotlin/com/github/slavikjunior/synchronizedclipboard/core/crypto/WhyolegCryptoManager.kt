package com.github.slavikjunior.synchronizedclipboard.core.crypto

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES

/**
 * AES-256-GCM через `dev.whyoleg.cryptography`.
 *
 * `provider-optimal` разворачивается в **CryptoKit** (с CommonCrypto как
 * fallback): вместо JCA используется системный фреймворк, поддерживающий
 * 128-битный GCM-tag — ровно тот же размер, что и JCA-реализация.
 *
 * Ключ деривируется из парольной фразы ([WhyolegPassphraseKeyDeriver]) и
 * декодируется в RAW-формате ([AES.Key.Format.RAW]) — поэтому шифротексты,
 * созданные на iOS, расшифровываются на Android/desktop с той же фразой
 * и наоборот. Пока фраза не задана — генерируется локальный ключ (этап
 * переходного состояния, device-local).
 *
 * Все операции — **blocking-варианты** whyoleg API: контракт [CryptoManager]
 * синхронный, а на Apple-провайдерах реальной приостановки не происходит.
 *
 * Формат строки задаёт общий [CipherTextCodec] — шифротексты совместимы
 * с JCA-реализацией.
 */
internal class WhyolegCryptoManager(
    private val passphraseProvider: PassphraseProvider,
) : CryptoManager {

    private val aesGcm = CryptographyProvider.Default.get(AES.GCM)

    // Кеш без @Volatile: на kotlin-native аннотация JVM-only, а гонка при
    // ленивой инициализации доброкачественная — повторная деривация даёт
    // тот же ключ, дескрипторы ссылочной памяти перезаписи не рвут.
    private var cachedPassphrase: String? = null

    private var cachedKey: AES.GCM.Key? = null

    override fun encrypt(plainText: String): String {
        // whyoleg возвращает `[IV | cipherText | tag]` — IV всегда 12 байт для
        // AES-GCM, как и в JCA. Разбиваем и кодируем через общий кодек.
        val combined = currentKey().cipher().encryptBlocking(plainText.encodeToByteArray())
        return CipherTextCodec.encode(
            iv = combined.copyOfRange(fromIndex = 0, toIndex = IV_LENGTH),
            cipherText = combined.copyOfRange(fromIndex = IV_LENGTH, toIndex = combined.size),
        )
    }

    override fun decrypt(cipherText: String): String {
        val decoded = CipherTextCodec.decode(cipherText) ?: return CryptoErrors.DECRYPTION_FAILED

        return try {
            // Склеиваем обратно в формат `[IV | cipherText | tag]`, который
            // ожидает whyoleg: IV в начале, tag в конце.
            val combined = decoded.iv + decoded.cipherText
            currentKey().cipher().decryptBlocking(combined).decodeToString()
        } catch (e: Exception) {
            CryptoErrors.DECRYPTION_FAILED
        }
    }

    private fun currentKey(): AES.GCM.Key {
        val passphrase = passphraseProvider.currentPassphrase()?.takeIf { it.isNotBlank() }
        if (cachedKey == null || cachedPassphrase != passphrase) {
            cachedPassphrase = passphrase
            cachedKey = passphrase?.let {
                val raw = WhyolegPassphraseKeyDeriver().derive(
                    passphrase = it,
                    salt = PassphraseKdf.SALT,
                    iterations = PassphraseKdf.ITERATIONS,
                    outputLengthBytes = PassphraseKdf.KEY_SIZE_BYTES,
                )
                aesGcm.keyDecoder().decodeFromByteArrayBlocking(AES.Key.Format.RAW, raw)
            } ?: aesGcm.keyGenerator(AES.Key.Size.B256).generateKeyBlocking()
        }
        return cachedKey!!
    }

    private companion object {
        /**
         * Длина IV в combined-формате whyoleg. 12 байт — стандартный nonce-size
         * AES-GCM (тот же, что генерирует JCA для `AES/GCM/NoPadding`).
         */
        const val IV_LENGTH = 12
    }
}
