package com.kis.youranimelist.model.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Anime(@JsonProperty("id") var id : Int,
                 @JsonProperty("title") var title : String,
                 @JsonProperty("main_picture") var mainPicture : MainPicture?,
                 @JsonProperty("start_season") var startSeason : StartSeason?,
                 @JsonProperty("mean") var mean : Float?,
                 @JsonProperty("synopsis") var synopsis : String? = "Description")
