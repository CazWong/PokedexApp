package com.example.jetpackcomposepokedex.network

import com.example.jetpackcomposepokedex.network.responses.Pokemon
import com.example.jetpackcomposepokedex.network.responses.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Uses retrofit to make the API call and sets behaviour
 */
interface RetrofitNetworkPokemonApi {

    @GET("pokemon")
    suspend fun getDataList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ) : PokemonList

    @GET("pokemon/{name}")
    suspend fun getDetailInfo(
        @Path("name") name: String
    ) : Pokemon
}