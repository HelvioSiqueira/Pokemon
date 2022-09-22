package com.example.pokemon

import com.google.gson.annotations.SerializedName

data class Pokemon(
    @SerializedName("name")
    var name: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("is_default")
    var is_default: Boolean = true,
    @SerializedName("height")
    var height: Int = 0,
    @SerializedName("stats")
    var status: List<Stat> = listOf(),
    var coverUrl: String = ""

) {
    override fun toString() = name
}

