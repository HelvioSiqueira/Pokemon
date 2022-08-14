package com.example.pokemon

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
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

    //Verifica se o dispositivo está conectado a internet
    @RequiresApi(Build.VERSION_CODES.M)
    fun hasConnection(ctx: Context): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val info = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(info)

        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    //Função principal que faz a conexãoe obtem o json
    fun loadPokemon(): List<Pokemon> {
        val pokemonList = mutableListOf<Pokemon>()
        val urlListPokemon: List<String>

        //Esse primeiro try tenta obter a lista de urls de todos os pokemon
        try {
            val connection = connect(POKE_HTTP_URL)
            val response_code = connection.responseCode

            if (response_code == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val json = JSONObject(streamToString(inputStream))

                urlListPokemon = readUrlPokemon(json)

                //Irá percorrer todas as url da lista para obter seu respectivo pokemon
                for (element in urlListPokemon) {
                    try {
                        val connection2 = connect(element)
                        val response_code2 = connection2.responseCode

                        if (response_code2 == HttpURLConnection.HTTP_OK) {
                            val inputStream2 = connection2.inputStream
                            val json2 = JSONObject(streamToString(inputStream2))

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

        //Retorna uma lista de pokemon(Por enquanto só o nome)
        return pokemonList
    }

    //Carrega o url de todos os pokemon a partir de uma lista de json
    @Throws(JSONException::class)
    fun readUrlPokemon(json: JSONObject): List<String> {
        val urlListPokemon = mutableListOf<String>()

        val jsonResults = json.getJSONArray("results")

        for (i in 0..151) {
            val jsonPoke = jsonResults.getJSONObject(i)
            val pokeUrl = jsonPoke.getString("url")

            urlListPokemon.add(pokeUrl)
        }

        return urlListPokemon
    }

    //Carrega o pokemon a partir do json
    @Throws(JSONException::class)
    fun readPokemonFromList(json: JSONObject): Pokemon {
        val jsonNome = json.getString("name")

        val jsonSprite = json.getJSONObject("sprites").getJSONObject("other").getJSONObject("official-artwork").getString("front_default")

        Log.i("HSV", jsonSprite)

        return Pokemon(jsonNome.replaceFirstChar { it.uppercase() }, jsonSprite)
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