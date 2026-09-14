package com.github.slavikjunior.synchronizedclipboard.core.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.HttpClientEngineConfig

/**
 * Platform-specific Ktor engine factory.
 * Android → OkHttp, iOS → Darwin (CFNetwork)
 */
internal expect fun getHttpClientEngine(): HttpClientEngineFactory<*>
