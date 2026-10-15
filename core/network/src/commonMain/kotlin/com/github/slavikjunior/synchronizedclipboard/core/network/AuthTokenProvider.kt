package com.github.slavikjunior.synchronizedclipboard.core.network

/**
 * Провайдер Firebase idToken для авторизации запросов к бэкенду
 * (`Authorization: Bearer <idToken>` — на REST и на WS-хендшейк).
 *
 * Реализации:
 *  - Android — Firebase Auth SDK (`FirebaseUser.getIdToken`), refresh по 401;
 *  - desktop CLI — Firebase Auth REST (`accounts:signInWithPassword` +
 *    `securetoken` refresh), токен и refreshToken в конфиг-файле.
 */
interface AuthTokenProvider {

    /**
     * Актуальный неистёкший idToken. Бросает исключение, если пользователь
     * не авторизован (см. [NotAuthorizedException]).
     */
    suspend fun validToken(): String

    /**
     * Принудительный refresh после 401: следующий [validToken] обязан
     * вернуть новый токен.
     */
    suspend fun invalidate()
}

class NotAuthorizedException : Exception("User is not authorized")
