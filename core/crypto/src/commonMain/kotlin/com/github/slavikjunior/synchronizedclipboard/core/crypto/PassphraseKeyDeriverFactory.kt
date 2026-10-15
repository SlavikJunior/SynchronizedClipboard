package com.github.slavikjunior.synchronizedclipboard.core.crypto

/**
 * Фабрика платформенной реализации [PassphraseKeyDeriver].
 * Нужна commonTest для прогона эталонных векторов PBKDF2 на каждом таргете.
 */
internal expect fun createPassphraseKeyDeriver(): PassphraseKeyDeriver
