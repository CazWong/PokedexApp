package com.example.jetpackcomposepokedex.network.responses


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionDetail(
    @SerialName("rarity")
    val rarity: Int,
    @SerialName("version")
    val version: Version
)