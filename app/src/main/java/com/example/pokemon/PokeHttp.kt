package com.example.pokemon

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

object PokeHttp {

    val POKE_HTTP_URL = "https://pokeapi.co/api/v2/pokemon?limit=100000&offset=0"

    @Throws(IOException::class)
    private fun connect(urlAddress: String): HttpURLConnection{
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun hasConnection(ctx: Context): Boolean{
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val info = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(info)

        return capabilities != null &&
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    fun loadPokemon(): List<Pokemon>{

        val pokemonList = mutableListOf<Pokemon>()
        val urlListPokemon: List<String>

        try {
            val connection = connect(POKE_HTTP_URL)
            val response_code = connection.responseCode

            if(response_code == HttpURLConnection.HTTP_OK){
                val inputStream = connection.inputStream
                val json = JSONObject(stramToString(inputStream))

                 urlListPokemon = readUrlPokemon(json)

                for(i in urlListPokemon.indices){
                    try {
                        val connection2 = connect(urlListPokemon[i])
                        val response_code2 = connection2.responseCode

                        if(response_code2 == HttpURLConnection.HTTP_OK){
                            val inputStream2 = connection2.inputStream
                            val json2 = JSONObject(stramToString(inputStream2))

                            pokemonList.add(readPokemonFromList(json2))
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pokemonList
    }

    @Throws(JSONException::class)
    fun readUrlPokemon(json: JSONObject) : List<String>{
        val urlListPokemon = mutableListOf<String>()

        val jsonResults = json.getJSONArray("results")

        for (i in 0 until jsonResults.length()){
            val jsonPoke = jsonResults.getJSONObject(i)
            val pokeUrl = jsonPoke.getString("url")

            urlListPokemon.add(pokeUrl)
        }

        return urlListPokemon
    }
    @Throws(JSONException::class)
    fun readPokemonFromList(json: JSONObject): Pokemon{
        val pokeList = mutableListOf<Pokemon>()

        val jsonNome = json.getString("name")

        return Pokemon(jsonNome)
    }

    @Throws(IOException::class)
    private fun stramToString(inputStrem: InputStream): String{
        val buffer = ByteArray(1024)
        val bigBuffer = ByteArrayOutputStream()

        var bytesRead: Int

        while (true){
            bytesRead = inputStrem.read(buffer)

            if (bytesRead == -1) break

            bigBuffer.write(buffer, 0, bytesRead)
        }
        return String(bigBuffer.toByteArray(), Charset.forName("UTF-8"))
    }
}