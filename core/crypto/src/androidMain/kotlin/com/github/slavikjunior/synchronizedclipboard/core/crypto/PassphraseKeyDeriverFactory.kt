package com.github.slavikjunior.synchronizedclipboard.core.crypto

internal actual fun createPassphraseKeyDeriver(): PassphraseKeyDeriver = JcaPassphraseKeyDeriver()
