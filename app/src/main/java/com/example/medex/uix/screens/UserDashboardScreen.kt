package com.example.medex.uix.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medex.viewmodel.MedexViewModel
import com.example.medex.data.Medicine
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    navController: NavController,
    medexViewModel: MedexViewModel
) {
    val cart = medexViewModel.cart // Observe cart state for recomposition
    // var showCheckoutDialog by remember { mutableStateOf(false) } // Remove this

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Dashboard") },
                actions = {
                    IconButton(onClick = { medexViewModel.logout(); navController.navigate(com.example.medex.uix.Routes.LOGIN) { popUpTo(com.example.medex.uix.Routes.USER_DASHBOARD) { inclusive = true } } }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (cart.isNotEmpty()) {
                BadgedBox(
                    badge = { Badge { Text(cart.size.toString()) } }
                ) {
                    FloatingActionButton(onClick = { navController.navigate("checkout") }) {
                        Icon(Icons.Filled.AddShoppingCart, contentDescription = "Checkout")
                    }
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (medicine.imageUrl != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(medicine.imageUrl),
                                    contentDescription = "Medicine Image",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .padding(bottom = 8.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Text(medicine.name, style = MaterialTheme.typography.titleMedium)
                            Text(medicine.description, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Price: $${medicine.price}", style = MaterialTheme.typography.bodySmall)
                                Text("Stock: ${medicine.stock}", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { medexViewModel.addToCart(medicine) },
                                enabled = medicine.stock > 0,
                                modifier = Modifier.fillMaxWidth()
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