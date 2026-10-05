package com.github.slavikjunior.synchronizedclipboard.core.crypto

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single

/**
 * AES-256-GCM через `dev.whyoleg.cryptography`.
 *
 * На iOS `provider-optimal` разворачивается в **CryptoKit** (с CommonCrypto как
 * fallback): вместо JCA используется системный фреймворк, поддерживающий
 * 128-битный GCM-tag — ровно тот же размер, что и Android-реализация.
 *
 * **Переходное состояние по ключу:** AES-ключ живёт в памяти процесса и
 * генерируется лениво при первом обращении к крипте. Хранение в Keychain не
 * решило бы задачу — расшифровка осталась бы device-local, а E2E-сценарий
 * подразумевает один ключ на все устройства пользователя. Постоянное хранилище
 * появится вместе с деривацией ключа из парольной фразы (PBKDF2); тогда ключ
 * будет одинаковым на Android и iOS.
 *
 * **Мост suspend → sync:** в whyoleg 0.6.0 AEAD-операции объявлены `suspend`
 * (единый API с асинхронными платформами, например WebCrypto), но контракт
 * домена [CryptoManager] синхронный — он потребляется из многочисленных
 * не-suspend мест репозитория. Мост делаем на границе платформы через
 * [runBlocking]: на Apple-провайдерах реальной приостановки не происходит
 * (CryptoKit вызывает синхронно), поэтому поток не блокируется надолго.
 *
 * Формат строки задаёт общий [CipherTextCodec] — шифротексты совместимы
 * с Android-реализацией.
 */
@Single
internal class CryptoManagerImpl : CryptoManager {

    private val aesGcm = CryptographyProvider.Default.get(AES.GCM)

    private val key: AES.GCM.Key by lazy {
        runBlocking { aesGcm.keyGenerator(AES.Key.Size.B256).generateKey() }
    }

    override fun encrypt(plainText: String): String {
        // whyoleg возвращает `[IV | cipherText | tag]` — IV всегда 12 байт для
        // AES-GCM, как и в JCA. Разбиваем и кодируем через общий кодек.
        val combined = runBlocking { key.cipher().encrypt(plainText.encodeToByteArray()) }
        return CipherTextCodec.encode(
            iv = combined.copyOfRange(fromIndex = 0, toIndex = IV_LENGTH),
            cipherText = combined.copyOfRange(fromIndex = IV_LENGTH, toIndex = combined.size),
        )
    }

    override fun decrypt(cipherText: String): String {
        val decoded = CipherTextCodec.decode(cipherText) ?: return DECRYPT_ERROR

        return try {
            // Склеиваем обратно в формат `[IV | cipherText | tag]`, который
            // ожидает whyoleg: IV в начале, tag в конце.
            val combined = decoded.iv + decoded.cipherText
            runBlocking { key.cipher().decrypt(combined) }.decodeToString()
        } catch (e: Exception) {
            DECRYPT_ERROR
        }
    }

    private companion object {
        /**
         * Длина IV в combined-формате whyoleg. 12 байт — стандартный nonce-size
         * AES-GCM (тот же, что генерирует JCA для `AES/GCM/NoPadding`).
         */
        const val IV_LENGTH = 12

        const val DECRYPT_ERROR = "🔒 [Ошибка расшифровки]"
    }
}
