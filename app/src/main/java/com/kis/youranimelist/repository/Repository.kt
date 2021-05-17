package com.kis.youranimelist.repository

import com.kis.youranimelist.model.Anime

interface Repository {
    fun getAnimeDataFromServer() : Map<String, List<Anime>>
    fun getAnimeDataFromLocalStorage() : Map<String, List<Anime>>
}