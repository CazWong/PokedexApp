package com.example.jetpackcomposepokedex.network.responses


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoveLearnMethod(
    @SerialName("name")
    val name: String,
    @SerialName("url")
    val url: String
)