package com.example.pokemon

import com.google.gson.annotations.SerializedName

data class Stat(
    @SerializedName("base_stat")
    var base_stat: Int = 0,
    @SerializedName("stat")
    var stat: ItemStat
)

data class ItemStat(
    @SerializedName("name")
    val name: String = "",
)

