package com.example.pokemon

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception
import java.util.concurrent.TimeUnit

object PokeHttp {

    val POKE_HTTP_URL = "https://pokeapi.co/api/v2/pokemon?limit=10&offset=0"

    fun loadPokemonGson(urlPokemon: String): Pokemon {

        val client = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(urlPokemon)
            .build()

        try {
            val response = client.newCall(request).execute()
            val json = response.body?.string()
            val gson = Gson()

            val publisher = gson.fromJson<Publisher>(json, Publisher::class.java)

            val pokemon = gson.fromJson<Pokemon>(json, Pokemon::class.java)

            pokemon.coverUrl = publisher.sprites.other.official_artwork.front_default
            pokemon.hp = publisher.stats[0].base_stat
            pokemon.attack = publisher.stats[1].base_stat
            pokemon.defense = publisher.stats[2].base_stat
            pokemon.specialAtack = publisher.stats[3].base_stat
            pokemon.specialDefense = publisher.stats[4].base_stat
            pokemon.speed = publisher.stats[5].base_stat

            Log.d("HSV", pokemon.attack.toString())

            return pokemon

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Pokemon()
    }

    //Verifica se o dispositivo está conectado a internet
    @RequiresApi(Build.VERSION_CODES.M)
    fun hasConnection(ctx: Context): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val info = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(info)

        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    //Função principal que faz a conexãoe obtem o json
    fun loadPokemon(): List<Pokemon>? {
        val pokemonList = mutableListOf<Pokemon>()
        val urlListPokemon = arrayListOf<String>()

        val client = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(POKE_HTTP_URL)
            .build()

        //Esse primeiro try tenta obter a lista de urls de todos os pokemon
        try {
            val response = client.newCall(request).execute()

            val json = response.body?.string()

            val gson = Gson()

            val results = gson.fromJson<Results>(json, Results::class.java)

            //Adiciona as urls no urlListPokemon
            results.results.forEach {
                urlListPokemon.add(it.url)
            }

            //Irá percorrer todas as url da lista para obter seu respectivo pokemon
            for (element in urlListPokemon) {
                try {
                    pokemonList.add(loadPokemonGson(element))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Retorna uma lista de pokemon(Por enquanto só o nome)
        return pokemonList
    }

    data class Url(
        @SerializedName("url")
        var url: String = ""
    )

    data class Results(
        @SerializedName("results")
        var results: List<Url> = emptyList()
    )
}