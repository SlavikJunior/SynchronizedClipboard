package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

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
class AuthModule
