package com.kis.youranimelist.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagingResponse(@SerialName("next") var next: String?)
