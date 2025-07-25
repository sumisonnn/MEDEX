package com.example.medex.uix.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medex.viewmodel.MedexViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog

@Composable
fun CheckoutScreen(navController: NavController, medexViewModel: MedexViewModel) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var paymentChecked by remember { mutableStateOf(false) }
    var cartItems by remember { mutableStateOf(medexViewModel.cart.toMutableList()) }

    val total = cartItems.sumOf { it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Checkout", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (cartItems.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Items in your cart", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    for (med in cartItems) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text("- ${med.name} (${med.description}) | $${med.price}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                cartItems = cartItems.toMutableList().also { it.remove(med) }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Total: $${"%.2f".format(total)}", style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    leadingIcon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = paymentChecked,
                        onCheckedChange = { paymentChecked = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Payment Method: Cash on Delivery", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        cartItems.forEach { med ->
                            medexViewModel.recordSale(
                                medicineId = med.id,
                                quantity = 1,
                                userName = name,
                                userAddress = address,
                                userPhone = phone
                            )
                        }
                        medexViewModel.clearCart()
                        cartItems = mutableListOf()
                        showSuccess = true
                        paymentChecked = false
                        showSuccessDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank() && address.isNotBlank() && phone.isNotBlank() && cartItems.isNotEmpty() && paymentChecked
                ) {
                    Text("Confirm Order")
                }
            }
        }
        if (showSuccess) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Order placed successfully!", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false; navController.popBackStack(com.example.medex.uix.Routes.USER_DASHBOARD, false) },
                title = { Text("Order Confirmed!") },
                text = { Text("Your order has been placed successfully.") },
                confirmButton = {
                    Button(onClick = { showSuccessDialog = false; navController.popBackStack(com.example.medex.uix.Routes.USER_DASHBOARD, false) }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}