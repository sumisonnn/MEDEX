package com.example.medex.data

data class Medicine(
    val id: String,
    var name: String,
    var description: String,
    var price: Double,
    var stock: Int
)