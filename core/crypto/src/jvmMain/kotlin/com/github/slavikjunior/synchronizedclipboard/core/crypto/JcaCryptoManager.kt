package com.github.slavikjunior.synchronizedclipboard.core.crypto

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

/**
 * AES-256-GCM (JCA) с ключом из PBKDF2-деривации парольной фразы.
 *
 *
 * Ключ кешируется вместе со снимком парольной фразы: смена фразы
 * пересобирает ключ при следующей операции, без пересоздания менеджера
 * (он синглтон в Koin).
 *
 * Ошибки декодирования формата и крипто-сбои наружу не выбрасываются —
 * наружу уходит только [CryptoErrors.DECRYPTION_FAILED].
 */
internal class JcaCryptoManager(
    private val passphraseProvider: PassphraseProvider,
) : CryptoManager {

    @Volatile
    private var cachedPassphrase: String? = null

    @Volatile
    private var cachedKey: SecretKeySpec? = null

    override fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, currentKey())
        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return CipherTextCodec.encode(iv = cipher.iv, cipherText = cipherText)
    }

    override fun decrypt(cipherText: String): String {
        val decoded = CipherTextCodec.decode(cipherText) ?: return CryptoErrors.DECRYPTION_FAILED

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                currentKey(),
                GCMParameterSpec(GCM_TAG_LENGTH, decoded.iv),
            )
            String(cipher.doFinal(decoded.cipherText), Charsets.UTF_8)
        } catch (e: Exception) {
            CryptoErrors.DECRYPTION_FAILED
        }
    }

    private fun currentKey(): SecretKeySpec {
        val passphrase = passphraseProvider.currentPassphrase()?.takeIf { it.isNotBlank() }
        if (cachedKey == null || cachedPassphrase != passphrase) {
            cachedPassphrase = passphrase
            val raw = passphrase
                ?.let { JcaPassphraseKeyDeriver().derive(it, PassphraseKdf.SALT, PassphraseKdf.ITERATIONS, PassphraseKdf.KEY_SIZE_BYTES) }
                ?: ephemeralKey()
            cachedKey = SecretKeySpec(raw, KEY_ALGORITHM)
        }
        return cachedKey!!
    }

    private fun ephemeralKey(): ByteArray =
        ByteArray(PassphraseKdf.KEY_SIZE_BYTES).also { SecureRandom().nextBytes(it) }

    private companion object {
        const val KEY_ALGORITHM = "AES"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_TAG_LENGTH = 128
    }
}
