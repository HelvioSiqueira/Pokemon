package com.example.pokemon

import com.google.gson.annotations.SerializedName

data class Other(
    @SerializedName("official-artwork")
    var official_artwork: OfficialArtwork
)