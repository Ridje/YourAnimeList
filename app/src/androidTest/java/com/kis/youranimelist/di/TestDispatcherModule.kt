package com.kis.youranimelist.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DispatcherModule::class])
object TestDispatcherModule {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @Dispatcher(dispatcher = YALDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = UnconfinedTestDispatcher()
}
