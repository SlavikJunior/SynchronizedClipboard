package com.github.slavikjunior.synchronizedclipboard.core.crypto

/**
 * Фабрика платформенной реализации [CryptoManager].
 *
 * Нужна для тестов из commonTest: типы реализаций объявлены в платформенных
 * (и промежуточных) source set'ах и не видны из общего кода.
 */
internal expect fun createCryptoManager(passphraseProvider: PassphraseProvider): CryptoManager
