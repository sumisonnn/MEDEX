package com.example.medex.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.medex.data.Medicine
import com.example.medex.data.Sale
import java.util.UUID
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MedexViewModel : ViewModel() {


    val medicines = mutableStateListOf<Medicine>()
    val sales = mutableStateListOf<Sale>()


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var currentUsername by mutableStateOf<String?>(null)
        private set
    var authError by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    init {
        // Add some sample data for demonstration
        addMedicine(Medicine(UUID.randomUUID().toString(), "Paracetamol", "Pain reliever", 2.50, 100))
        addMedicine(Medicine(UUID.randomUUID().toString(), "Amoxicillin", "Antibiotic", 15.00, 50))
        addMedicine(Medicine(UUID.randomUUID().toString(), "Ibuprofen", "Anti-inflammatory", 5.00, 75))
        // Set current user if already logged in
        currentUsername = auth.currentUser?.email
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


    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        isLoading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    currentUsername = auth.currentUser?.email
                    authError = null
                    onResult(true)
                } else {
                    authError = task.exception?.localizedMessage ?: "Login failed."
                    onResult(false)
                }
            }
    }

    fun signup(email: String, password: String, onResult: (Boolean) -> Unit) {
        isLoading = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    currentUsername = auth.currentUser?.email
                    authError = null
                    onResult(true)
                } else {
                    authError = task.exception?.localizedMessage ?: "Signup failed."
                    onResult(false)
                }
            }
    }

    fun logout() {
        auth.signOut()
        currentUsername = null
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}