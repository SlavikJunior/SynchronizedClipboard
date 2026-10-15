package com.github.slavikjunior.synchronizedclipboard.core.crypto

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * PBKDF2-HMAC-SHA256 (RFC 8018 §5.2) на базе JCA `Mac`.
 *
 * Реализован вручную, а не через `SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")`:
 * этот алгоритм появился в Android только с API 26, а minSdk проекта — 24.
 * `Mac.getInstance("HmacSHA256")` доступен с API 1 и даёт побитно тот же
 * результат, что и платформный PBKDF2, поэтому ключи совместимы с
 * whyoleg-реализацией на iOS (RFC-соответствие).
 *
 * Использование из нескольких потоков безопасно: каждый вызов создаёт
 * собственный экземпляр [Mac] (он не потокобезопасен).
 */
internal class JcaPassphraseKeyDeriver : PassphraseKeyDeriver {

    override fun derive(passphrase: String, salt: String, iterations: Int, outputLengthBytes: Int): ByteArray {
        require(iterations > 0) { "PBKDF2 iterations must be positive" }
        require(outputLengthBytes > 0) { "PBKDF2 output length must be positive" }

        val passphraseBytes = passphrase.toByteArray(Charsets.UTF_8)
        val saltBytes = salt.toByteArray(Charsets.UTF_8)

        val output = ByteArray(outputLengthBytes)
        val block = ByteArray(SHA256_OUTPUT_BYTES)

        var written = 0
        var blockIndex = 1
        while (written < outputLengthBytes) {
            // U1 = PRF(P, S || INT_32_BE(blockIndex)).
            // doFinal() сбрасывает Mac в исходное состояние, поэтому один
            // экземпляр переиспользуется для всех раундов этого блока.
            val mac = createMac(passphraseBytes)
            mac.update(saltBytes)
            mac.update(blockIndex.toBigEndianBytes())
            var u = mac.doFinal()
            System.arraycopy(u, 0, block, 0, SHA256_OUTPUT_BYTES)

            // U2..Uc: Uj = PRF(P, U(j-1)), результирующий блок = XOR всех Uj
            for (round in 2..iterations) {
                u = mac.doFinal(u)
                for (i in 0 until SHA256_OUTPUT_BYTES) {
                    block[i] = (block[i].toInt() xor u[i].toInt()).toByte()
                }
            }

            val toCopy = minOf(SHA256_OUTPUT_BYTES, outputLengthBytes - written)
            System.arraycopy(block, 0, output, written, toCopy)
            written += toCopy
            blockIndex++
        }

        passphraseBytes.fill(0)
        return output
    }

    private fun createMac(passphraseBytes: ByteArray): Mac =
        Mac.getInstance(HMAC_ALGORITHM).apply {
            init(SecretKeySpec(passphraseBytes, HMAC_ALGORITHM))
        }

    private fun Int.toBigEndianBytes(): ByteArray = byteArrayOf(
        (this ushr 24).toByte(),
        (this ushr 16).toByte(),
        (this ushr 8).toByte(),
        this.toByte(),
    )

    private companion object {
        const val HMAC_ALGORITHM = "HmacSHA256"
        const val SHA256_OUTPUT_BYTES = 32
    }
}
