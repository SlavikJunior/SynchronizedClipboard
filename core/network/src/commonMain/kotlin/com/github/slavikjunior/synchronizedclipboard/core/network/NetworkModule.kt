package com.github.slavikjunior.synchronizedclipboard.core.network

import com.github.slavikjunior.synchronizedclipboard.core.network.api.NetworkSyncApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json as SerializationJson
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin DI-модуль для сетевого слоя (KMP).
 *
 * Аннотации Koin (`@Module`, `@Single`) обрабатываются
 * **Koin Compiler Plugin** (Kotlin Compiler Plugin) на compile-time.
 */
@Module
class NetworkModule {

    /**
     * HTTP-клиент Ktor с platform-specific engine, ContentNegotiation (JSON), Logging и WebSockets.
     * @Single = singleton в Koin graph.
     *
     * Логирование на уровне INFO (без HEADERS): в заголовках ездит
     * `Authorization: Bearer <idToken>` — он не должен попадать в логи.
     */
    @Single
    fun httpClient(
        json: SerializationJson,   // kotlinx-serialization Json, передаётся Koin-ом (см. JsonModule в :app)
    ): HttpClient = HttpClient(getHttpClientEngine()) {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = LogLevel.INFO
        }
        install(WebSockets) {
        }
    }

    /**
     * Явный DSL-модуль Koin: регистрирует HTTP-клиент и NetworkSyncApi.
     *
     * Вместо `@ComponentScan` (сгенерированный модуль не используется),
     * чтобы избежать проблем с доступом к сгенерированному `.module` в текущей
     * версии Koin Compiler Plugin.
     *
     * Зависимости [SyncConfigProvider], [AuthTokenProvider] и
     * [DeviceIdentityProvider] — интерфейсы из общего кода; их реализации
     * регистрируются клиентским слоем (Android: feature:auth/feature:settings,
     * desktop CLI: конфиг-файл).
     */
    fun networkModule() = module {
        single { httpClient(get()) }
        single<NetworkSyncApi> {
            KtorNetworkSyncApi(
                httpClient = get(),
                json = get(),
                syncConfigProvider = get(),
                tokenProvider = get(),
                deviceIdentityProvider = get(),
                defaultDispatcher = get(named("default_dispatcher")),
            )
        }
    }
}
