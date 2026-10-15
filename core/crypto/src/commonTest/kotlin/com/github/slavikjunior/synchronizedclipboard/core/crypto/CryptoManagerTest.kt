package com.github.slavikjunior.synchronizedclipboard.core.crypto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Тесты контракта E2E-шифрования кросс-устройствами.
 *
 * Ключевой риск, который закрывают эти тесты: PBKDF2-деривация на Android/JVM
 * (ручная реализация через `Mac("HmacSHA256")` — из-за minSdk 24) и на iOS
 * (whyoleg/CryptoKit) должны давать **идентичный ключ** из одной парольной
 * фразы. Проверяем эталонным вектором PBKDF2-HMAC-SHA256 и round-trip'ом
 * через два независимых менеджера (эмуляция Mac ↔ Android).
 *
 * commonTest компилируется против commonMain, поэтому платформенные
 * реализации берутся через фабрики [createPassphraseKeyDeriver] и
 * [createCryptoManager] — тест выполняется на каждом таргете модуля.
 */
class CryptoManagerTest {

    @Test
    fun `pbkdf2 matches reference vectors`() {
        // Эталоны для PBKDF2-HMAC-SHA256 (SHA-256, не SHA-1 из RFC 6070),
        // сверены с hashlib.pbkdf2_hmac:
        // 1) c=1, dkLen=64 — покрывает multi-block логику (T1 + T2 с counter=2);
        // 2) c=2, dkLen=32 — покрывает XOR-раунды (U1 ^ U2).
        val deriver = createPassphraseKeyDeriver()

        val singleRoundTwoBlocks = deriver
            .derive(passphrase = "password", salt = "salt", iterations = 1, outputLengthBytes = 64)
            .toHexString()
        assertEquals(PBKDF2_C1_64B_HEX, singleRoundTwoBlocks)

        val twoRounds = deriver
            .derive(passphrase = "password", salt = "salt", iterations = 2, outputLengthBytes = 32)
            .toHexString()
        assertEquals(PBKDF2_C2_32B_HEX, twoRounds)
    }

    @Test
    fun `cross-device roundtrip with same passphrase`() {
        val mac = createCryptoManager(PassphraseProvider { PASSPHRASE })
        val android = createCryptoManager(PassphraseProvider { PASSPHRASE })

        val encrypted = mac.encrypt(PLAINTEXT)

        assertEquals(PLAINTEXT, android.decrypt(encrypted))
    }

    @Test
    fun `decrypt fails on different passphrase`() {
        val mac = createCryptoManager(PassphraseProvider { PASSPHRASE })
        val android = createCryptoManager(PassphraseProvider { "another-passphrase-entirely" })

        val encrypted = mac.encrypt(PLAINTEXT)

        assertEquals(CryptoErrors.DECRYPTION_FAILED, android.decrypt(encrypted))
    }

    @Test
    fun `same passphrase on independent managers gives different ciphertexts`() {
        // PBKDF2-ключ один, но IV уникален на каждую шифровку — одна и та же
        // фраза не должна приводить к одинаковым шифротекстам.
        val first = createCryptoManager(PassphraseProvider { PASSPHRASE })
        val second = createCryptoManager(PassphraseProvider { PASSPHRASE })

        val encryptedFirst = first.encrypt(PLAINTEXT)
        val encryptedSecond = second.encrypt(PLAINTEXT)

        assertNotEquals(encryptedFirst, encryptedSecond)
        assertEquals(PLAINTEXT, second.decrypt(encryptedFirst))
    }

    @Test
    fun `manager without passphrase still encrypts and decrypts locally`() {
        // Переходное поведение: фраза не задана → эфемерный ключ (device-local).
        val manager = createCryptoManager(PassphraseProvider { null })

        val encrypted = manager.encrypt(PLAINTEXT)

        assertEquals(PLAINTEXT, manager.decrypt(encrypted))
        val other = createCryptoManager(PassphraseProvider { null })
        assertEquals(CryptoErrors.DECRYPTION_FAILED, other.decrypt(encrypted))
    }

    @Test
    fun `key is re-derived when passphrase changes`() {
        var passphrase: String? = PASSPHRASE
        val manager = createCryptoManager(PassphraseProvider { passphrase })

        val encryptedOld = manager.encrypt(PLAINTEXT)
        assertEquals(PLAINTEXT, manager.decrypt(encryptedOld))

        // Меняем фразу: старый шифротекст должен перестать расшифровываться,
        // а новый — расшифровываться тем же менеджером (код пересобран).
        passphrase = "completely-new-passphrase"
        assertEquals(CryptoErrors.DECRYPTION_FAILED, manager.decrypt(encryptedOld))
        assertEquals(PLAINTEXT, manager.decrypt(manager.encrypt(PLAINTEXT)))
    }

    private companion object {
        const val PASSPHRASE = "correct horse battery staple"
        const val PLAINTEXT = "Привет с Mac — кириллица и эмодзи 🔐 должны пережить round-trip"

        /**
         * PBKDF2-HMAC-SHA256, P="password", S="salt", c=1, dkLen=64.
         */
        const val PBKDF2_C1_64B_HEX =
            "120fb6cffcf8b32c43e7225256c4f837a86548c92ccc35480805987cb70be17b" +
                "4dbf3a2f3dad3377264bb7b8e8330d4efc7451418617dabef683735361cdc18c"

        /**
         * PBKDF2-HMAC-SHA256, P="password", S="salt", c=2, dkLen=32.
         */
        const val PBKDF2_C2_32B_HEX = "ae4d0c95af6b46d32d0adff928f06dd02a303f8ef3c251dfd6e2d85a95474c43"
    }

    private fun ByteArray.toHexString(): String =
        joinToString("") { byte -> ((byte.toInt() and 0xFF).toString(16)).padStart(2, '0') }
}
