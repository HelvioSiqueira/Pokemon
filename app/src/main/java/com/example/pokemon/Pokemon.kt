package com.example.pokemon

import com.google.gson.annotations.SerializedName

data class Pokemon(
    @SerializedName("name")
    var nome: String = "",
    //var hp: Int = 0,
    //var attack: Int = 0,
    //var defense: Int = 0,
    //var specialAtack: Int = 0,
    //var specialDefense: Int = 0,
    @SerializedName("sprites")
    var imagem: Sprites

) {
    override fun toString() = nome
}