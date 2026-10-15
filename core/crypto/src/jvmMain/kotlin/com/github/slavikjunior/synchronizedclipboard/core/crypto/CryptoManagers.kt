package com.github.slavikjunior.synchronizedclipboard.core.crypto

internal actual fun createCryptoManager(passphraseProvider: PassphraseProvider): CryptoManager =
    JcaCryptoManager(passphraseProvider)
