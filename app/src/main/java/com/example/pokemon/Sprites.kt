package com.example.pokemon

import com.google.gson.annotations.SerializedName

data class Sprites(
    @SerializedName("other")
    var other: Other
)
