package com.example.pokemon

import com.google.gson.annotations.SerializedName

data class Sprites(
    @SerializedName("other")
    var other: Other
)

data class Other(
    @SerializedName("official-artwork")
    var official_artwork: OfficialArtwork
)

data class OfficialArtwork(
    @SerializedName("front_default")
    var front_default: String = ""
)
