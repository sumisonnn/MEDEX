package com.example.medex.uix.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.background
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Delete
import com.example.medex.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    navController: NavController,
    medexViewModel: MedexViewModel
) {
    var searchText by remember { mutableStateOf("") }
    val categories = listOf("Colds & Flu", "Allergies", "Diarrhea")
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showDetail by remember { mutableStateOf<Medicine?>(null) }
    val promos = listOf("Rapid COVID-19 Antigen Self-Test", "Shop Now")
    var selectedTab by remember { mutableStateOf(0) } // 0: Home, 1: Cart, 2: Profile
    var showAddedMessage by remember { mutableStateOf(false) }
    var addedMessageText by remember { mutableStateOf("") }
    var addedMessageTrigger by remember { mutableStateOf(0L) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(Icons.Filled.LocationOn, contentDescription = "Home") }, label = { Text("Home") })
                NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") }, label = { Text("Cart") })
                NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") }, label = { Text("Profile") })
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(12.dp)
            ) {
                when (selectedTab) {
                    0 -> { // Home/Medicine tab
                        // Top bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.LocationOn, contentDescription = "Location")
                                Text("My Home", style = MaterialTheme.typography.titleMedium)
                            }
                            IconButton(onClick = { medexViewModel.logout(); navController.navigate(com.example.medex.uix.Routes.LOGIN) { popUpTo(com.example.medex.uix.Routes.USER_DASHBOARD) { inclusive = true } } }) {
                                Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Welcome message
                        val welcomeName = medexViewModel.profileName
                        if (welcomeName.isNotBlank()) {
                            Text("Welcome, $welcomeName!", style = MaterialTheme.typography.titleLarge)
                        } else {
                            Text("Welcome!", style = MaterialTheme.typography.titleLarge)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Search bar
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            label = { Text("Enter drug name to search") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        // Promo cards
                        val promoImages = listOf(
                            R.drawable.medex,
                            R.drawable.medico,
                            R.drawable.splashlogo,
                            R.drawable.ic_launcher_foreground
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            promoImages.forEach { imageResId ->
                                PromoCard(imageResId = imageResId)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Filter chips
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { category ->
                                AssistChip(
                                    onClick = { selectedCategory = if (selectedCategory == category) null else category },
                                    label = { Text(category) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (selectedCategory == category) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = if (selectedCategory == category) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Product grid
                        val filteredMedicines = medexViewModel.medicines.filter {
                            (searchText.isBlank() || it.name.contains(searchText, ignoreCase = true)) &&
                                    (selectedCategory == null || it.category.equals(selectedCategory, ignoreCase = true))
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            items(filteredMedicines) { medicine ->
                                ProductCard(medicine = medicine, onAddToCart = {
                                    medexViewModel.addToCart(medicine)
                                    addedMessageText = "Added to cart"
                                    showAddedMessage = true
                                    addedMessageTrigger = System.currentTimeMillis()
                                })
                            }
                        }
                        // Hide message after delay
                        LaunchedEffect(addedMessageTrigger) {
                            if (showAddedMessage) {
                                kotlinx.coroutines.delay(2000)
                                showAddedMessage = false
                            }
                        }
                        // Placeholder for detail view
                        if (showDetail != null) {
                            AlertDialog(
                                onDismissRequest = { showDetail = null },
                                title = { Text(showDetail!!.name) },
                                text = { Text("Product details coming soon!") },
                                confirmButton = {
                                    Button(onClick = { showDetail = null }) { Text("Close") }
                                }
                            )
                        }
                    }
                    1 -> { // Cart tab
                        Text("My Cart", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (medexViewModel.cart.isEmpty()) {
                            Text("Your cart is empty.", style = MaterialTheme.typography.bodyLarge)
                        } else {
                            LazyColumn {
                                items(medexViewModel.cart) { medicine ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
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
                                                    modifier = Modifier
                                                        .size(60.dp)
                                                        .clip(MaterialTheme.shapes.medium),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(medicine.name, style = MaterialTheme.typography.titleMedium)
                                                Text("$${medicine.price}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                            }
                                            IconButton(onClick = { medexViewModel.removeFromCart(medicine) }) {
                                                Icon(Icons.Filled.Delete, contentDescription = "Remove from cart")
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.navigate(com.example.medex.uix.Routes.CHECKOUT) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Checkout")
                            }
                        }
                    }
                    2 -> { // Profile tab
                        val context = LocalContext.current
                        val vm = medexViewModel
                        var name by remember { mutableStateOf(vm.profileName) }
                        var email by remember { mutableStateOf(vm.profileEmail) }
                        var phone by remember { mutableStateOf(vm.profilePhone) }
                        var showSaved by remember { mutableStateOf(false) }
                        var isLoading by remember { mutableStateOf(false) }
                        var error by remember { mutableStateOf<String?>(null) }

                        // Load profile when tab is selected
                        LaunchedEffect(Unit) {
                            vm.loadUserProfile()
                            name = vm.profileName
                            email = vm.profileEmail
                            phone = vm.profilePhone
                        }

                        Text("Edit Profile", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            isLoading = true
                            error = null
                            vm.updateUserProfile(name, email, phone) { success ->
                                isLoading = false
                                showSaved = success
                                if (!success) error = "Failed to save profile."
                            }
                        }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
                            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else Text("Save")
                        }
                        if (showSaved) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Profile saved!", color = MaterialTheme.colorScheme.primary)
                        }
                        if (error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(error!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            // Centered add-to-cart message overlay
            if (showAddedMessage) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color(0xCC222222),
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Added to cart successfully!",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}