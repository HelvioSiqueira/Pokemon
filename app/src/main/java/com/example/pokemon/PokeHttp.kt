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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

object PokeHttp {

    val POKE_HTTP_URL = "https://pokeapi.co/api/v2/pokemon?limit=20&offset=0"

    //Estabelece a coneção a partir da url recebida
    @Throws(IOException::class)
    private fun connect(urlAddress: String): HttpURLConnection {
        val second = 100
        val url = URL(urlAddress)

        val connection = (url.openConnection() as HttpURLConnection).apply {
            readTimeout = 10 * second
            connectTimeout = 15 * second
            requestMethod = "GET"
            doInput = true
            doOutput = false
        }
        connection.connect()
        return connection
    }

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

    //Carrega o url de todos os pokemon a partir de uma lista de json
    @Throws(JSONException::class)
    fun readUrlPokemon(json: JSONObject): List<String> {
        val urlListPokemon = mutableListOf<String>()

        val jsonResults = json.getJSONArray("results")

        for (i in 0..20) {
            val jsonPoke = jsonResults.getJSONObject(i)
            val pokeUrl = jsonPoke.getString("url")

            urlListPokemon.add(pokeUrl)
        }

        return urlListPokemon
    }

    @Throws(IOException::class)
    private fun streamToString(inputStrem: InputStream): String {
        val buffer = ByteArray(1024)
        val bigBuffer = ByteArrayOutputStream()

        var bytesRead: Int

        while (true) {
            bytesRead = inputStrem.read(buffer)

            if (bytesRead == -1) break

            bigBuffer.write(buffer, 0, bytesRead)
        }
        return String(bigBuffer.toByteArray(), Charset.forName("UTF-8"))
    }
}