package com.github.slavikjunior.synchronizedclipboard.core.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.github.slavikjunior.synchronizedclipboard.core.di")
class DiModule {

    @Single
    @Named("io_dispatcher")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Single
    @Named("main_dispatcher")
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Single
    @Named("default_dispatcher")
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Single
    @Named("main_immediate_dispatcher")
    fun provideMainImmediateDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate
}
