package com.example.jetpackcomposepokedex.di

import com.example.jetpackcomposepokedex.data.PokemonRepository
import com.example.jetpackcomposepokedex.network.RetrofitNetworkPokemonApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Instructs Dagger/Hilt on how to dependency inject into our module & viewmodel
 * ...These singleton components will live only as long our app does
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    const val BASE_URL = "https://pokeapi.co/api/v2/"

    @Singleton
    @Provides
    fun providePokemonRepository(
        api: RetrofitNetworkPokemonApi
    ) = PokemonRepository(api)

    @Singleton
    @Provides
    fun providePokemonApi() : RetrofitNetworkPokemonApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(RetrofitNetworkPokemonApi::class.java)
    }
}

