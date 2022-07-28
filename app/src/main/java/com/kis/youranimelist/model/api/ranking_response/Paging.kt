package com.kis.youranimelist.model.api.ranking_response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Paging(@SerialName("next") var next: String)
