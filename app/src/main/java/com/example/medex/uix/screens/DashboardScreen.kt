package com.example.medex.uix.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medex.R
import com.example.medex.uix.Routes
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medex.viewmodel.MedexViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, medexViewModel: MedexViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MEDEX Dashboard") },
                actions = {
                    IconButton(onClick = { medexViewModel.logout(); navController.navigate(Routes.LOGIN) { popUpTo(Routes.DASHBOARD) { inclusive = true } } }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.medex),
                contentDescription = "MEDEX Logo",
                modifier = Modifier
                    .height(120.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = "Welcome, Admin",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navController.navigate(Routes.MEDICINE_LIST) },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.LocalPharmacy, contentDescription = "Medicines", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.height(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Manage Medicines", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navController.navigate(Routes.SALES_LIST) },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.History, contentDescription = "Sales History", tint = Color(0xFF388E3C), modifier = Modifier.height(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("View Sales History", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(navController = rememberNavController())
}
