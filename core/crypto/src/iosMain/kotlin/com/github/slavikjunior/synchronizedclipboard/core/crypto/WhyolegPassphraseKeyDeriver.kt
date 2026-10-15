package com.github.slavikjunior.synchronizedclipboard.core.crypto

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.BinarySize.Companion.bytes
import dev.whyoleg.cryptography.algorithms.PBKDF2
import dev.whyoleg.cryptography.algorithms.SHA256

/**
 * PBKDF2-HMAC-SHA256 (RFC 8018) через whyoleg cryptography.
 *
 * RFC-эквивалентна JCA-реализации ([JcaPassphraseKeyDeriver]) при тех же
 * параметрах — это проверяется тестами по эталонным векторам RFC 6070
 * в commonTest (выполняются на каждом таргете).
 */
internal class WhyolegPassphraseKeyDeriver : PassphraseKeyDeriver {

    private val pbkdf2 = CryptographyProvider.Default.get(PBKDF2)

    override fun derive(passphrase: String, salt: String, iterations: Int, outputLengthBytes: Int): ByteArray =
        pbkdf2.secretDerivation(
            digest = SHA256,
            iterations = iterations,
            outputSize = outputLengthBytes.bytes,
            salt = salt.encodeToByteArray(),
        ).deriveSecretToByteArrayBlocking(passphrase.encodeToByteArray())
}
