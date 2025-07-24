package com.example.medex.data

data class Medicine(
    val id: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var stock: Int = 0,
    var imageUrl: String? = null,
    var category: String = ""
)