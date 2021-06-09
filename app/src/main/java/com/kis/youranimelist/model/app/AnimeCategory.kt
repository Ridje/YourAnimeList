package com.kis.youranimelist.model.app

import com.kis.youranimelist.model.api.ranking_response.AnimeRanked

data class AnimeCategory(val name: String, var animeList: MutableList<Anime> = mutableListOf(),
                         val networkGetter: ()-> List<AnimeRanked>)
