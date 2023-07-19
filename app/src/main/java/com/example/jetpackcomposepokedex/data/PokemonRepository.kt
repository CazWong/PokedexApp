package com.example.jetpackcomposepokedex.data

import com.example.jetpackcomposepokedex.network.RetrofitNetworkPokemonApi
import com.example.jetpackcomposepokedex.network.responses.Pokemon
import com.example.jetpackcomposepokedex.network.responses.PokemonList
import com.example.jetpackcomposepokedex.util.UiState
import dagger.hilt.android.scopes.ActivityScoped
import java.io.IOException
import javax.inject.Inject

@ActivityScoped // lives as long as the app does
class PokemonRepository @Inject constructor(
    private val api: RetrofitNetworkPokemonApi
) {
    suspend fun getPokemonList(limit: Int, offset: Int): UiState<PokemonList> {
        val response = try {
            api.getDataList(limit, offset)
        } catch (e: IOException) {
            return UiState.Error("Something went PokeWrong!")
        }
        return UiState.Success(response)
    }

    suspend fun getPokemonInfo(name: String): UiState<Pokemon> {
        val response = try {
            api.getDetailInfo(name)
        } catch (e: IOException) {
            return UiState.Error("Something went PokeWrong!")
        }
        return UiState.Success(response)
    }
}





