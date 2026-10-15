package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.data.local

import com.github.slavikjunior.synchronizedclipboard.core.network.AuthTokenProvider
import com.github.slavikjunior.synchronizedclipboard.core.network.NotAuthorizedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

/**
 * Android-реализация [AuthTokenProvider] поверх [AuthDataStore].
 *
 * Токен пока «заглушка» (AuthRepositoryImpl сохраняет фиктивные значения) —
 * реальный Firebase idToken появится вместе с Firebase Auth. Контракт при этом
 * соблюдается полностью:
 *  - [validToken] отдаёт сохранённый токен либо бросает [NotAuthorizedException];
 *  - [invalidate] — no-op, так как токен читается из хранилища на каждый вызов
 *    и refresh-механизма (Firebase `getIdToken(forceRefresh = true)`) ещё нет.
 *
 * Чтение `EncryptedSharedPreferences` — блокирующий disk I/O, поэтому
 * выполняется на `io_dispatcher`.
 */
@Single
internal class DataStoreAuthTokenProvider(
    private val authDataStore: AuthDataStore,
    @Named("io_dispatcher") private val ioDispatcher: CoroutineDispatcher,
) : AuthTokenProvider {

    override suspend fun validToken(): String = withContext(ioDispatcher) {
        authDataStore.currentAccessToken() ?: throw NotAuthorizedException()
    }

    override suspend fun invalidate() {
        // Refresh не поддерживается: следующий validToken() и так читает
        // актуальное значение из хранилища.
    }
}
