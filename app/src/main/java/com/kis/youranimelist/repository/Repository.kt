package com.kis.youranimelist.repository

import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.model.AnimeCategory

interface Repository {
    fun getAnimeListByCategory() : List<AnimeCategory>
    fun getAnimeInfo() : Anime
}