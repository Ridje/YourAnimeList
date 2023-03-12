package com.kis.youranimelist.domain.cache

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface RefreshCacheUseCase {
    fun observeRefreshEvents(): SharedFlow<Unit>

    suspend fun onEttiSettingSwitched()
}

class RefreshCacheUseCaseImpl: RefreshCacheUseCase {

    private val refreshCachesFlow = MutableSharedFlow<Unit>(replay = 1)

    override fun observeRefreshEvents(): SharedFlow<Unit> {
        return refreshCachesFlow
    }

    override suspend fun onEttiSettingSwitched() {
        refreshCachesFlow.emit(Unit)
    }
}
