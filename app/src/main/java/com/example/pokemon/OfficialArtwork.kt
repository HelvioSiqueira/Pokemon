package com.example.pokemon

import com.google.gson.annotations.SerializedName

data class OfficialArtwork(
    @SerializedName("front_default")
    var front_default: String = ""
)
