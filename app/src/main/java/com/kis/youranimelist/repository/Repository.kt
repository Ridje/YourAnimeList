package com.kis.youranimelist.repository

import com.kis.youranimelist.model.Anime

interface Repository {
    fun getAnimeListByGroup() : List<Map<String, Any>>
}