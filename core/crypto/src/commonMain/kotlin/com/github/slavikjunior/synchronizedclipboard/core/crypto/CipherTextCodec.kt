package com.github.slavikjunior.synchronizedclipboard.core.crypto

import kotlin.io.encoding.Base64

/**
 * Кодек строкового представления шифротекста: `Base64(IV):Base64(cipherText)`.
 *
 * **Единый контракт формата для Android и iOS.** Обе реализации ([CryptoManager])
 * обязаны разбивать вывод на IV и шифротекст и склеивать через этот кодек —
 * только так элементы, зашифрованные на Android, расшифровываются на iOS и
 * наоборот. Base64 без переносов строк.
 *
 * `cipherText` содержит GCM-tag (16 байт) « appended», как это делает
 * `Cipher.doFinal` в JCA и `encrypt()` в whyoleg — порядок
 * `[IV | cipherText | tag]` совпадает на обеих платформах.
 */
internal object CipherTextCodec {

    private const val SEPARATOR = ":"

    /**
     * Склеивает IV и шифротекст в транспортную строку.
     *
     * Алфавит [Base64.Default] идентичен `android.util.Base64` с флагом
     * `NO_WRAP`, поэтому записи, созданные до KMP-миграции, читаются без изменений.
     */
    fun encode(iv: ByteArray, cipherText: ByteArray): String =
        Base64.Default.encode(iv) + SEPARATOR + Base64.Default.encode(cipherText)

    /**
     * Разбирает транспортную строку.
     *
     * Возвращает `null`, если формат нарушен: разделителей не два, одна из
     * частей пустая или Base64 не декодировался. Вызывающий код трактует
     * `null` как повреждённые данные и подставляет [CryptoManager.decrypt]-ошибку —
     * исключения наружу не выбрасываются.
     */
    fun decode(value: String): Decoded? {
        val parts = value.split(SEPARATOR)
        if (parts.size != 2) return null
        if (parts.any { it.isEmpty() }) return null

        return try {
            Decoded(
                iv = Base64.Default.decode(parts[0]),
                cipherText = Base64.Default.decode(parts[1]),
            )
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    /**
     * Разобранная пара «IV + cipherText» (cipherText уже содержит GCM-tag).
     */
    class Decoded(
        val iv: ByteArray,
        val cipherText: ByteArray,
    )
}
