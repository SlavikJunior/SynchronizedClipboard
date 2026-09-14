package com.github.slavikjunior.synchronizedclipboard.core.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun getHttpClientEngine(): HttpClientEngineFactory<*> = OkHttp
