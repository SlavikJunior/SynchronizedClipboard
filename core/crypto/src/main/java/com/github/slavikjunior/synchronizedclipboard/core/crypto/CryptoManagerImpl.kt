package com.github.slavikjunior.synchronizedclipboard.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64
import org.koin.core.annotation.Single

@Single
internal class CryptoManagerImpl : CryptoManager {

    private val keyAlias = "syncclip_clipboard_key"
    private val androidKeyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }

    override fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return "${Base64.encodeToString(iv, Base64.NO_WRAP)}:${Base64.encodeToString(cipherText, Base64.NO_WRAP)}"
    }

    override fun decrypt(cipherText: String): String {
        return try {
            val parts = cipherText.split(":")
            if (parts.size != 2) return DECRYPT_ERROR

            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val encrypted = Base64.decode(parts[1], Base64.NO_WRAP)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_LENGTH, iv))
            String(cipher.doFinal(encrypted), Charsets.UTF_8)
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
