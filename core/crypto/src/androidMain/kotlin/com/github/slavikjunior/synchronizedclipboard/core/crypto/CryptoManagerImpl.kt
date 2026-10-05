package com.github.slavikjunior.synchronizedclipboard.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import org.koin.core.annotation.Single

/**
 * AES-256-GCM с ключом в AndroidKeyStore (JCA).
 *
 * Реализация не переведена на `dev.whyoleg.cryptography`: ключ AndroidKeyStore
 * неэкспортируем, API whyoleg работает с экспортируемыми ключами (RAW-формат).
 * Замена означала бы отказ от аппаратного хранения — недопустимо.
 *
 * Строковый формат задаёт общий [CipherTextCodec], поэтому шифротекст
 * совместим с iOS-реализацией.
 *
 * Ошибки декодирования формата и крипто-сбои наружу не выбрасываются —
 * наружу уходит только [DECRYPT_ERROR].
 */
@Single
internal class CryptoManagerImpl : CryptoManager {

    private val keyAlias = "syncclip_clipboard_key"
    private val androidKeyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }

    override fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return CipherTextCodec.encode(iv = cipher.iv, cipherText = cipherText)
    }

    override fun decrypt(cipherText: String): String {
        val decoded = CipherTextCodec.decode(cipherText) ?: return DECRYPT_ERROR

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                getOrCreateKey(),
                GCMParameterSpec(GCM_TAG_LENGTH, decoded.iv),
            )
            String(cipher.doFinal(decoded.cipherText), Charsets.UTF_8)
        } catch (e: Exception) {
            DECRYPT_ERROR
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val existingKey = androidKeyStore.getKey(keyAlias, null) as? SecretKey
        if (existingKey != null) return existingKey

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private companion object {
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_TAG_LENGTH = 128
        const val DECRYPT_ERROR = "🔒 [Ошибка расшифровки]"
    }
}
