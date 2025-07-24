package com.example.medex.uix.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medex.viewmodel.MedexViewModel
import com.example.medex.data.Medicine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    navController: NavController,
    medexViewModel: MedexViewModel
) {
    var cart by remember { mutableStateOf(mutableListOf<Medicine>()) }
    var showCheckoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (cart.isNotEmpty()) {
                FloatingActionButton(onClick = { navController.navigate("checkout") }) {
                    Icon(Icons.Filled.AddShoppingCart, contentDescription = "Checkout")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Available Medicines", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(medexViewModel.medicines) { medicine ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(medicine.name, style = MaterialTheme.typography.titleMedium)
                                Text(medicine.description, style = MaterialTheme.typography.bodySmall)
                                Text("Price: $${medicine.price}", style = MaterialTheme.typography.bodySmall)
                                Text("Stock: ${medicine.stock}", style = MaterialTheme.typography.bodySmall)
                            }
                            Button(
                                onClick = { cart.add(medicine) },
                                enabled = medicine.stock > 0
                            ) {
                                Text("Add to Cart")
                            }
                        }
                    }
                }
            }
        }
    }
}