package com.kis.youranimelist.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagingResponse(@SerialName("next") var next: String?)
