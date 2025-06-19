package com.example.medex.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.medex.data.Medicine
import com.example.medex.data.Sale
import java.util.UUID
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MedexViewModel : ViewModel() {


    val medicines = mutableStateListOf<Medicine>()
    val sales = mutableStateListOf<Sale>()


    private val users = mutableMapOf<String, String>()
    var currentUsername by mutableStateOf<String?>(null)
        private set

    init {
        // Add some sample data for demonstration
        addMedicine(Medicine(UUID.randomUUID().toString(), "Paracetamol", "Pain reliever", 2.50, 100))
        addMedicine(Medicine(UUID.randomUUID().toString(), "Amoxicillin", "Antibiotic", 15.00, 50))
        addMedicine(Medicine(UUID.randomUUID().toString(), "Ibuprofen", "Anti-inflammatory", 5.00, 75))

        // Add some sample users for login
        users["admin"] = "password"
        users["user1"] = "pass123"
    }

    fun addMedicine(medicine: Medicine) {
        medicines.add(medicine)
    }

    fun updateMedicine(updatedMedicine: Medicine) {
        val index = medicines.indexOfFirst { it.id == updatedMedicine.id }
        if (index != -1) {
            medicines[index] = updatedMedicine
        }
    }

    fun deleteMedicine(medicineId: String) {
        medicines.removeIf { it.id == medicineId }
    }

    fun getMedicineById(medicineId: String?): Medicine? {
        return medicines.find { it.id == medicineId }
    }

    fun recordSale(medicineId: String, quantity: Int) {
        val medicine = getMedicineById(medicineId)
        if (medicine != null && medicine.stock >= quantity) {
            val newStock = medicine.stock - quantity
            updateMedicine(medicine.copy(stock = newStock))
            val sale = Sale(UUID.randomUUID().toString(), medicineId, quantity, System.currentTimeMillis())
            sales.add(sale)
        } else {
            // Handle insufficient stock or medicine not found
            println("Error: Cannot record sale. Insufficient stock or medicine not found.")
        }
    }

    fun getSalesForMedicine(medicineId: String): List<Sale> {
        return sales.filter { it.medicineId == medicineId }
    }


    fun login(username: String, password: String): Boolean {
        if (users.containsKey(username) && users[username] == password) {
            currentUsername = username
            return true
        }
        return false
    }

    fun signup(username: String, password: String): Boolean {
        if (users.containsKey(username)) {
            return false
        }
        users[username] = password
        currentUsername = username
        return true
    }

    fun logout() {
        currentUsername = null
    }

    fun isLoggedIn(): Boolean {
        return currentUsername != null
    }
}