package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl

import android.content.Context
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.data.local.AuthDataStore
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/**
 * Koin DI-модуль auth-фичи.
 *
 * `@Module` + `@ComponentScan` — Koin Compiler Plugin на compile-time рекурсивно
 * сканирует пакет `...feature.auth.impl` на наличие `@Single` / `@Factory` / `@KoinViewModel`
 * и генерирует Koin module.
 *
 * **Публичный класс** — :app добавляет его в startKoin { modules(...) }.
 */
@Module
@ComponentScan("com.github.slavikjunior.synchronizedclipboard.feature.auth.impl")
class AuthModule {

    /**
     * [AuthDataStore] — единственный хранитель токенов сессии. Конструктор
     * приватный (синглтон через [AuthDataStore.getInstance]), поэтому бин
     * создаётся явной фабрикой, а не аннотацией на классе.
     */
    @Single
    fun authDataStore(context: Context): AuthDataStore = AuthDataStore.getInstance(context)
}
