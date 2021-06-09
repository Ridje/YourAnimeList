package com.kis.youranimelist.model.app

import android.os.Parcelable
import com.kis.youranimelist.model.api.Anime
import com.kis.youranimelist.model.api.MainPicture
import com.kis.youranimelist.model.api.StartSeason
import com.kis.youranimelist.model.api.ranking_response.AnimeRankingItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Anime(
    var id: Int,
    var title: String,
    var mainPicture: MainPicture?,
    var startSeason: StartSeason?,
    var mean: Float?,
    var synopsis: String? = "Description"
) : Parcelable {
    constructor(animeRanked: AnimeRankingItem) : this(
        animeRanked.id,
        animeRanked.title,
        animeRanked.mainPicture,
        null,
        null,
        null
    )

    constructor(anime: Anime) : this(anime.id, anime.title, anime.mainPicture, anime.startSeason , anime.mean, anime.synopsis)
}
