package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl

import com.github.slavikjunior.synchronizedclipboard.core.cache.LruReactiveCache
import com.github.slavikjunior.synchronizedclipboard.core.cache.ReactiveCache
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl")
class ClipboardModule {

    /**
     * Типизированный LRU-кеш для элементов буфера обмена.
     * Используется как источник истины для UI в [ClipboardRepositoryImpl].
     */
    @Single
    @Named("ClipboardLRU")
    fun provideClipboardLRU(): ReactiveCache<String, ClipboardItem> =
        LruReactiveCache(maxSize = 100)
}
