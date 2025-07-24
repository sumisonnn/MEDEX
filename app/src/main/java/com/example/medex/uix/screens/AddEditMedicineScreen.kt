package com.example.medex.uix.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medex.data.Medicine
import com.example.medex.viewmodel.MedexViewModel
import java.util.UUID
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMedicineScreen(
    navController: NavController,
    medexViewModel: MedexViewModel = viewModel(),
    medicineId: String? = null
) {
    val isEditing = medicineId != null
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploading = true
            uploadError = null
            // Upload to Cloudinary
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val client = OkHttpClient()
            val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", tempFile.name, requestBody)
                .addFormDataPart("upload_preset", "medex_unsigned") // <-- Replaced with your preset
                .build()
            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/dq89u0ok2/image/upload") // <-- Replaced with your cloud name
                .post(body)
                .build()
            Thread {
                try {
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        val url = Regex("""secure_url":"([^"]+)""").find(responseBody)?.groupValues?.get(1)
                            ?: Regex("""url":"([^"]+)""").find(responseBody)?.groupValues?.get(1)
                        if (url != null) {
                            imageUrl = url
                        } else {
                            uploadError = "No image URL returned"
                        }
                    } else {
                        uploadError = "Upload failed"
                    }
                } catch (e: Exception) {
                    uploadError = e.localizedMessage
                } finally {
                    uploading = false
                }
            }.start()
        }
    }

    LaunchedEffect(medicineId) {
        if (isEditing) {
            val medicine = medexViewModel.getMedicineById(medicineId)
            medicine?.let {
                name = it.name
                description = it.description
                price = it.price.toString()
                stock = it.stock.toString()
                imageUrl = it.imageUrl
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Medicine" else "Add Medicine") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Medicine Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Medicine Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uploading
            ) {
                Text(if (uploading) "Uploading..." else "Pick Image")
            }
            if (uploadError != null) {
                Text(uploadError ?: "", color = Color.Red)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val parsedPrice = price.toDoubleOrNull() ?: 0.0
                    val parsedStock = stock.toIntOrNull() ?: 0

                    if (isEditing) {
                        medexViewModel.updateMedicine(
                            Medicine(
                                id = medicineId!!,
                                name = name,
                                description = description,
                                price = parsedPrice,
                                stock = parsedStock,
                                imageUrl = imageUrl
                            )
                        )
                    } else {
                        medexViewModel.addMedicine(
                            Medicine(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                description = description,
                                price = parsedPrice,
                                stock = parsedStock,
                                imageUrl = imageUrl
                            )
                        )
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Save Changes" else "Add Medicine")
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (isEditing) {
                Button(
                    onClick = {
                        val currentMedicine = medexViewModel.getMedicineById(medicineId)
                        if (currentMedicine != null) {
                            medexViewModel.recordSale(
                                currentMedicine.id,
                                1,
                                userName = "",
                                userAddress = "",
                                userPhone = ""
                            ) // Record sale of 1 unit
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Record Sale (1 unit)")
                }
            }
        }
    }
}