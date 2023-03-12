package com.kis.youranimelist.domain.cache

import com.kis.youranimelist.data.cache.AnimeRankingMemoryCache
import javax.inject.Inject

interface ClearCacheUseCase {
    operator fun invoke()
}

class ClearCacheUseCaseImpl @Inject constructor(
    private val cacheFactory: AnimeRankingMemoryCache.Factory,
): ClearCacheUseCase {
    override fun invoke() {
        cacheFactory.clearCaches()
    }
}
