package com.example.jetpackcomposepokedex.network.responses


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerationI(
    @SerialName("red-blue")
    val redBlue: RedBlue,
    @SerialName("yellow")
    val yellow: Yellow
)