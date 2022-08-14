package com.example.pokemon

data class Pokemon(
    var nome: String = "",
    //var hp: Int = 0,
    //var attack: Int = 0,
    //var defense: Int = 0,
    //var specialAtack: Int = 0,
    //var specialDefense: Int = 0,
    var imagem: String = ""

) {
    override fun toString() = nome
}