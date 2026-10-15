package com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.data.local

import com.github.slavikjunior.synchronizedclipboard.core.crypto.PassphraseProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

/**
 * Android-реализация [PassphraseProvider] для E2E-шифрования.
 *
 * Хранит парольную фразу в оперативном кеше, который обновляется подпиской
 * на DataStore: смена фразы в настройках немедленно подхватывается
 * [com.github.slavikjunior.synchronizedclipboard.core.crypto.CryptoManager]
 * (синглтон) без пересоздания и рестарта приложения.
 *
 * Сама фраза персистится как обычная строка в DataStore — это осознанное
 * упрощение MVP. Post-MVP: хранить производный ключ в AndroidKeyStore-обёрнутом
 * EncryptedSharedPreferences (ключ из фразы всё равно пере-выводим, поэтому
 * аппаратное хранение не ломает E2E между устройствами).
 */
@Single
internal class DataStorePassphraseProvider(
    private val settingsDataStore: SettingsDataStore,
    @Named("io_dispatcher") ioDispatcher: CoroutineDispatcher,
) : PassphraseProvider {

    private val scope = CoroutineScope(ioDispatcher + SupervisorJob())

    @Volatile
    private var cached: String = ""

    init {
        scope.launch {
            settingsDataStore.observePassphrase().collect { passphrase ->
                cached = passphrase
            }
        }
    }

    override fun currentPassphrase(): String? = cached.takeIf { it.isNotBlank() }

    fun clear() {
        scope.cancel()
    }
}
