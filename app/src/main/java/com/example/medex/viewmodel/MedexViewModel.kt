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
import com.google.firebase.database.*

class MedexViewModel : ViewModel() {


    val medicines = mutableStateListOf<Medicine>()
    val sales = mutableStateListOf<Sale>()

    // Cart state
    val cart = mutableStateListOf<Medicine>()
    fun addToCart(medicine: Medicine) {
        cart.add(medicine)
    }
    fun removeFromCart(medicine: Medicine) {
        cart.remove(medicine)
    }
    fun clearCart() {
        cart.clear()
    }


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val medicinesRef: DatabaseReference = db.getReference("medicines")
    private val salesRef: DatabaseReference = db.getReference("sales")

    var currentUsername by mutableStateOf<String?>(null)
        private set
    var authError by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var userRole by mutableStateOf<String?>(null)

    init {
        // Listen for medicines changes
        medicinesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                medicines.clear()
                for (medSnap in snapshot.children) {
                    val med = medSnap.getValue(Medicine::class.java)
                    if (med != null) medicines.add(med)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        // Listen for sales changes
        salesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sales.clear()
                for (saleSnap in snapshot.children) {
                    val sale = saleSnap.getValue(Sale::class.java)
                    if (sale != null) sales.add(sale)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        // Set current user if already logged in
        currentUsername = auth.currentUser?.email
    }

    fun addMedicine(medicine: Medicine) {
        medicinesRef.child(medicine.id).setValue(medicine)
    }

    fun updateMedicine(updatedMedicine: Medicine) {
        medicinesRef.child(updatedMedicine.id).setValue(updatedMedicine)
    }

    fun deleteMedicine(medicineId: String) {
        medicinesRef.child(medicineId).removeValue()
    }

    fun getMedicineById(medicineId: String?): Medicine? {
        return medicines.find { it.id == medicineId }
    }

    fun recordSale(
        medicineId: String,
        quantity: Int,
        userName: String,
        userAddress: String,
        userPhone: String
    ) {
        val medicine = getMedicineById(medicineId)
        if (medicine != null && medicine.stock >= quantity) {
            val newStock = medicine.stock - quantity
            updateMedicine(medicine.copy(stock = newStock))
            val sale = Sale(
                id = java.util.UUID.randomUUID().toString(),
                medicineId = medicineId,
                quantity = quantity,
                saleDate = System.currentTimeMillis(),
                userName = userName,
                userAddress = userAddress,
                userPhone = userPhone
            )
            salesRef.child(sale.id).setValue(sale)
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
                    // Fetch user role from database
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        db.getReference("users").child(uid).child("role").get().addOnSuccessListener { snap ->
                            userRole = snap.getValue(String::class.java)
                            onResult(true)
                        }.addOnFailureListener {
                            userRole = null
                            onResult(true)
                        }
                    } else {
                        userRole = null
                        onResult(true)
                    }
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
                    // Store user role in database (default to 'user')
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        db.getReference("users").child(uid).child("role").setValue("user")
                        userRole = "user"
                    }
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