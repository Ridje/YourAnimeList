package com.kis.youranimelist.model.api.ranking_response

import com.fasterxml.jackson.annotation.JsonProperty

data class Paging(@JsonProperty("next") var next : String)