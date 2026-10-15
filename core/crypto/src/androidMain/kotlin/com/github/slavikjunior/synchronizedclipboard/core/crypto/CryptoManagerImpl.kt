package com.github.slavikjunior.synchronizedclipboard.core.crypto

import org.koin.core.annotation.Single

/**
 * Koin-биндинг [CryptoManager] для Android и JVM (desktop).
 *
 * Делегирует в общий [JcaCryptoManager]. Парольная фраза приходит из
 * [PassphraseProvider], реализация которого зависит от клиента:
 * `:feature:settings:impl` (Android) или конфигурационный файл (desktop CLI).
 */
@Single
internal class CryptoManagerImpl(
    passphraseProvider: PassphraseProvider,
) : CryptoManager by JcaCryptoManager(passphraseProvider)
