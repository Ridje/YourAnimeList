package com.kis.youranimelist.model.ranking_response

import com.fasterxml.jackson.annotation.JsonProperty

data class Paging(@JsonProperty("next") var next : String)