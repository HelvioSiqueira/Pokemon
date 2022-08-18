package com.example.pokemon

import com.google.gson.annotations.SerializedName

data class Pokemon(
    @SerializedName("name")
    var name: String = "",
    //var hp: Int = 0,
    //var attack: Int = 0,
    //var defense: Int = 0,
    //var specialAtack: Int = 0,
    //var specialDefense: Int = 0,
    //var speed: Int = 0,
    var coverUrl: String = ""

    ) {
    override fun toString() = name
}

