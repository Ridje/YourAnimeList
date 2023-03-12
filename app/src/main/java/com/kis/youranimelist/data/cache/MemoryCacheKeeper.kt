package com.kis.youranimelist.data.cache

import com.kis.youranimelist.domain.rankinglist.model.Anime
import com.kis.youranimelist.ui.model.CachingKey

private const val CACHE_INVALIDATION_TIME: Long = 86400 / 2
private const val MS_IN_SEC: Int = 1000

interface MemoryCacheKeeper<T, S> {
    val updatedAt: Long
    val lifeTime: Long
    val cache: T?

    fun updateCache(key: Int, value: S)
    fun invalidateCache()
}

class AnimeRankingMemoryCache(
    override val lifeTime: Long,
) : MemoryCacheKeeper<Map<Int, List<Anime>>, List<Anime>> {

    private var _updatedAt: Long = System.currentTimeMillis()
    override val updatedAt: Long
        get() = _updatedAt

    private val _cache: HashMap<Int, List<Anime>> = HashMap()
    override val cache: Map<Int, List<Anime>>?
        get() {
            return if (System.currentTimeMillis() - lifeTime * 1000 > updatedAt) {
                _cache.clear()
                null
            } else {
                _cache
            }
        }

    override fun updateCache(key: Int, value: List<Anime>) {
        _updatedAt = System.currentTimeMillis()
        _cache[key] = value
    }

    override fun invalidateCache() {
        if (System.currentTimeMillis() - lifeTime * MS_IN_SEC > updatedAt) {
            _cache.clear()
        }
    }

    class Factory {
        private val cachedRanks = mutableMapOf<CachingKey, AnimeRankingMemoryCache>()
        fun getOrCreate(
            key: CachingKey,
        ): AnimeRankingMemoryCache {
            return cachedRanks.getOrPut(key) { AnimeRankingMemoryCache(CACHE_INVALIDATION_TIME) }
        }

        fun clearCaches() {
            cachedRanks.clear()
        }
    }
}
