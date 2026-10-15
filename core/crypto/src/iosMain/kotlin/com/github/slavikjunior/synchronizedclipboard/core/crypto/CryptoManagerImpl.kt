package com.github.slavikjunior.synchronizedclipboard.core.crypto

import org.koin.core.annotation.Single

/**
 * Koin-биндинг [CryptoManager] для iOS.
 *
 * Делегирует в [WhyolegCryptoManager] (CryptoKit). Парольная фраза приходит
 * из [PassphraseProvider] — реализация появится вместе с iOS-клиентом
 * (пока iOS-таргет используется для тестов общего кода).
 */
@Single
internal class CryptoManagerImpl(
    passphraseProvider: PassphraseProvider,
) : CryptoManager by WhyolegCryptoManager(passphraseProvider)
