package com.github.slavikjunior.synchronizedclipboard.core.cache.di

import com.github.slavikjunior.synchronizedclipboard.core.cache.LruReactiveCache
import com.github.slavikjunior.synchronizedclipboard.core.cache.ReactiveCache
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.github.slavikjunior.synchronizedclipboard.core.cache")
class CacheModule {

    /**
     * Generic LRU-кеш без привязки к конкретному типу значения.
     * Типизированные бинды (например, для [ClipboardItem]) регистрируются
     * в feature-модуле через квалификатор [org.koin.core.qualifier.named].
     */
    @Single
    fun provideLruReactiveCache(): ReactiveCache<String, Any> =
        LruReactiveCache(maxSize = 100)
}
