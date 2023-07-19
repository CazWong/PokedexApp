package com.example.jetpackcomposepokedex.ui.screens

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.jetpackcomposepokedex.data.PokemonRepository
import com.example.jetpackcomposepokedex.model.PokedexEntry
import com.example.jetpackcomposepokedex.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * In charge of the states(keeping track of info shown to user)
 * and logic (behaviour and operations behind the scenes)
 */

const val NUMBER_OF_POKEMONS_FETCHED =20

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    // Starting counter for pagination
    private var currentPage = 0

    // Objects and properties to keep track of changing states
    // These could have been placed in the uiState data class
    var pokemonListResults = mutableStateOf<List<PokedexEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endOfResults = mutableStateOf(false)

    init {
        getPokemonsAndPaginate()
    }

    fun getPokemonsAndPaginate() {
        viewModelScope.launch {
            isLoading.value = true
            // 1. Loads pokemons list ( 20, 0 * 20)
            val listResult = pokemonRepository.getPokemonList(NUMBER_OF_POKEMONS_FETCHED, currentPage * NUMBER_OF_POKEMONS_FETCHED)
            // 2. Account for loading and error handling if something goes wrong with fetching data

            when (listResult) {
                is UiState.Success -> {
                    // when successful the data will have loaded &
                    // endOfResults will turn true if it meets the following condition:
                    //      number of pages and number of pokemons fetched is greater than number of list results. ex: (  220  >= 200 )
                    endOfResults.value = currentPage * NUMBER_OF_POKEMONS_FETCHED >= listResult.data!!.count
                    // 1. Get image Url for each pokemon
                    val getPokemonListEntries = listResult.data.results.map { result ->
                        val pokemonIndex = if(result.url.endsWith("/")) {
                            result.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            result.url.takeLastWhile { it.isDigit() }
                        }
                        val url =  "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemonIndex}.png"
                        PokedexEntry(result.name.replaceFirstChar { it.uppercase() }, imageUrl = url, number = pokemonIndex.toInt())
                    }
                    // When successful, reset States of variables to
                    currentPage++
                    loadError.value = ""
                    isLoading.value = false
                    pokemonListResults.value = pokemonListResults.value + getPokemonListEntries
                }
                is UiState.Error -> {
                    loadError.value = listResult.message!!
                    isLoading.value = false
                }
            }
        }
    }

    // Generates dominant colour from image with androidx palette
    fun getDominantColour(drawable: Drawable, onFinish: (Color) -> Unit) {
        // configures pokemon image to a compatible image type that can be used with the androidx library
        val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bitmap).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}