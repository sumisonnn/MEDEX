package com.example.medex.uix.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medex.viewmodel.MedexViewModel

@Composable
fun CheckoutScreen(navController: NavController, medexViewModel: MedexViewModel) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    val cart = medexViewModel.cart

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Checkout", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Payment Method: Cash on Delivery", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                cart.forEach { med ->
                    medexViewModel.recordSale(med.id, 1)
                }
                medexViewModel.clearCart()
                showSuccess = true
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && address.isNotBlank() && phone.isNotBlank() && cart.isNotEmpty()
        ) {
            Text("Confirm Order")
        }
        if (showSuccess) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Order placed successfully!", color = MaterialTheme.colorScheme.primary)
        }
    }
}