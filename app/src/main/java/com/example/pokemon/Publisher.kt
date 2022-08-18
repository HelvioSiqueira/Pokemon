package com.example.pokemon

import com.google.gson.annotations.SerializedName

data class Publisher(
    @SerializedName("sprites")
    var sprites: Sprites
)