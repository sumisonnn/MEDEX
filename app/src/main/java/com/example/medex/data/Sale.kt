

package com.example.medex.data

import java.util.Date

data class Sale(
    val id: String,
    val medicineId: String,
    val quantity: Int,
    val saleDate: Long
)