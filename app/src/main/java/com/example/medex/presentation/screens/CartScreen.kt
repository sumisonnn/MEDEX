package com.example.medex.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.medex.viewmodel.MedexViewModel

@Composable
fun CartScreen(medexViewModel: MedexViewModel, onCheckout: () -> Unit) {
    val cart = medexViewModel.cart
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Cart", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        if (cart.isEmpty()) {
            Text("Your cart is empty.", style = MaterialTheme.typography.bodyLarge)
        } else {
            cart.forEach { medicine ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (medicine.imageUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(medicine.imageUrl),
                                contentDescription = "Medicine Image",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(medicine.name, style = MaterialTheme.typography.titleMedium)
                            Text("$${medicine.price}", style = MaterialTheme.typography.bodyMedium)
                        }
                        // Quantity is always 1 in this app, but you can add controls here
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
                Text("Checkout")
            }
        }
    }
}