package com.kis.youranimelist.data.cache

import com.kis.youranimelist.domain.rankinglist.model.Anime

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
        if (System.currentTimeMillis() - lifeTime * 1000 > updatedAt) {
            _cache.clear()
        }
    }

    class Factory {
        private val cachedRanks = mutableMapOf<String, AnimeRankingMemoryCache>()
        fun getOrCreate(
            rankingType: String,
        ): AnimeRankingMemoryCache {
            return cachedRanks.getOrPut(rankingType) { AnimeRankingMemoryCache(86400) }
        }
    }
}
