package com.github.slavikjunior.synchronizedclipboard.core.crypto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Тесты единого контракта формата [CipherTextCodec].
 *
 * Запускаются в commonTest — то есть на каждом таргете модуля. Это защита
 * главного риска KMP-миграции: расхождения формата `Base64(IV):Base64(cipherText)`
 * между Android- (`android.util.Base64` до миграции) и iOS-реализациями.
 */
class CipherTextCodecTest {

    @Test
    fun `encode then decode returns original bytes`() {
        val iv = ByteArray(IV_LENGTH) { index -> index.toByte() }
        val cipherText = byteArrayOf(1, 2, 3, 4, 5, 16, 17)

        val decoded = CipherTextCodec.decode(CipherTextCodec.encode(iv, cipherText))

        assertNotNull(decoded)
        assertEquals(iv.toList(), decoded.iv.toList())
        assertEquals(cipherText.toList(), decoded.cipherText.toList())
    }

    @Test
    fun `encoded value has exactly two base64 parts`() {
        val encoded = CipherTextCodec.encode(ByteArray(IV_LENGTH) { 7 }, byteArrayOf(9))

        val parts = encoded.split(":")
        assertEquals(2, parts.size)
        // Base64 без переносов строк
        assertTrue(encoded.none { it == '\n' || it == '\r' })
    }

    @Test
    fun `decode returns null for malformed input`() {
        assertNull(CipherTextCodec.decode("без-разделителя"))
        assertNull(CipherTextCodec.decode("aaa:bbb:ccc"))
        assertNull(CipherTextCodec.decode("!!!не-base64!!!:данные"))
        assertNull(CipherTextCodec.decode("AAAA:"))
        assertNull(CipherTextCodec.decode(""))
    }

    private companion object {
        const val IV_LENGTH = 12
    }
}
