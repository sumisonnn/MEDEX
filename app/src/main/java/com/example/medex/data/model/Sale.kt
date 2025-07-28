package com.example.medex.data.model

data class Sale(
    val id: String = "",
    val medicineId: String = "",
    val quantity: Int = 0,
    val saleDate: Long = 0L,
    val userName: String = "",
    val userAddress: String = "",
    val userPhone: String = ""
)