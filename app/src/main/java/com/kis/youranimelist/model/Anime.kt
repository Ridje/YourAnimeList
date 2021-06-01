package com.kis.youranimelist.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.kis.youranimelist.model.ranking_response.AnimeRankingItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Anime(@JsonProperty("id") var id : Int,
                 @JsonProperty("title") var title : String,
                 @JsonProperty("main_picture") var mainPicture : MainPicture?,
                 @JsonProperty("start_season") var startSeason : StartSeason?,
                 @JsonProperty("mean") var mean : Float?,
                 @JsonProperty("synopsis") var synopsis : String? = "Description") : Parcelable {
                 constructor(animeRanked : AnimeRankingItem) : this(animeRanked.id, animeRanked.title, animeRanked.mainPicture, null, null, null)
}
