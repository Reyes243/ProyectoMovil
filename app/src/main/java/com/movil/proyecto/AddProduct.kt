
package com.movil.proyecto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.movil.proyecto.ui.theme.*

class AddProduct : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val editMode = intent.getBooleanExtra("EDIT_MODE", false)
        val plantId = intent.getStringExtra("PLANT_ID") ?: ""
        
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                AddProductScreen(
                    editMode = editMode,
                    existingPlantId = plantId,
                    onBack = { finish() },
                    onLogout = { 
                        UserManager.logout()
                        val intent = Intent(this, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun AddProductScreen(
    editMode: Boolean = false,
    existingPlantId: String = "",
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("10") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("PLANTAS DE INTERIOR") }
    var expanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var existingImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(editMode, existingPlantId) {
        if (editMode && existingPlantId.isNotEmpty()) {
            val product = ProductManager.getUserProducts().find { it.id == existingPlantId }
            if (product != null) {
                name = product.name
                priceText = product.price.replace("$", "").trim()
                stockText = product.stock.toString()
                description = product.description
                category = product.category
                existingImageUrl = product.imageUrl
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            Surface(color = ColorVerdeOlivaOscuro, shadowElevation = 4.dp) {
                Column(modifier = Modifier.padding(top = 44.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Raíz Viva", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ColorCremaCampos)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = ColorCremaCampos, modifier = Modifier.padding(horizontal = 8.dp).size(24.dp))
                            Icon(Icons.Default.ShoppingCart, null, tint = ColorCremaCampos, modifier = Modifier.padding(horizontal = 8.dp).size(24.dp))
                            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = ColorCremaCampos, modifier = Modifier.padding(horizontal = 8.dp).size(24.dp).clickable { onLogout() })
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 16.dp, bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = ColorCremaCampos) }
                        OutlinedTextField(
                            value = "", onValueChange = {}, placeholder = { Text("Buscar...", fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = ColorCremaCampos, unfocusedContainerColor = ColorCremaCampos)
                        )
                    }
                }
            }
        },
        containerColor = ColorFondoVerdeClaro
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = ColorCremaCampos) {
                Text(
                    text = if (editMode) "EDITAR PRODUCTO" else "AGREGAR PRODUCTO", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center, 
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = ColorCremaCampos)) {
                Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                    
                    Box(modifier = Modifier.size(120.dp).background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).clickable { galleryLauncher.launch("image/*") }, contentAlignment = Alignment.Center) {
                        if (selectedImageUri != null) {
                            AsyncImage(model = selectedImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else if (existingImageUrl != null) {
                             AsyncImage(model = existingImageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddAPhoto, null, tint = Color.Gray)
                                Text("Foto", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    AddProductField("Nombre", name, { name = it }, "Ej. Monstera")
                    
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text("Categoría", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ColorVerdeOlivaOscuro)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { 
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                            )
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                listOf("PLANTAS DE INTERIOR", "PLANTAS DE EXTERIOR", "BAJO MANTENIMIENTO", "AROMÁTICAS Y COMESTIBLES", "MACETAS Y ACCESORIOS", "CUIDADOS Y BIENESTAR").forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            category = cat
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text("Precio", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ColorVerdeOlivaOscuro)
                                OutlinedTextField(
                                    value = priceText, 
                                    onValueChange = { input ->
                                        val filtered = input.filter { it.isDigit() || it == '.' }
                                        if (filtered.count { it == '.' } <= 1) {
                                            if (filtered.contains(".")) {
                                                val parts = filtered.split(".")
                                                if (parts.size == 1 || parts[1].length <= 2) {
                                                    priceText = filtered
                                                }
                                            } else {
                                                priceText = filtered
                                            }
                                        }
                                    }, 
                                    modifier = Modifier.fillMaxWidth(),
                                    prefix = { Text("$ ", fontWeight = FontWeight.Bold, color = Color.Black) },
                                    placeholder = { Text("0", fontSize = 12.sp) }, 
                                    shape = RoundedCornerShape(12.dp), 
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White, 
                                        unfocusedContainerColor = Color.White,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black
                                    )
                                )
                            }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            AddProductField(
                                label = "Stock", 
                                value = stockText, 
                                onValueChange = { if (it.all { c -> c.isDigit() }) stockText = it }, 
                                placeholder = "10",
                                keyboardType = KeyboardType.Number
                            )
                        }
                    }
                    
                    AddProductField("Descripción", description, { description = it }, "Detalles del producto...")

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (isLoading) {
                        CircularProgressIndicator(color = ColorNaranjaAccion)
                        Text("Guardando...", fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    } else {
                        Button(
                            onClick = { 
                                if (name.isNotBlank() && priceText.isNotBlank()) {
                                    isLoading = true
                                    CoroutineScope(Dispatchers.Main).launch {
                                        val success = ProductManager.saveProduct(
                                            context = context,
                                            id = if (editMode) existingPlantId else null,
                                            name = name, 
                                            price = priceText, 
                                            category = category, 
                                            imageUri = selectedImageUri,
                                            stock = stockText.toIntOrNull() ?: 0,
                                            description = description,
                                            existingImageUrl = existingImageUrl
                                        )
                                        isLoading = false
                                        if (success) {
                                            Toast.makeText(context, "¡Producto guardado exitosamente!", Toast.LENGTH_SHORT).show()
                                            onBack()
                                        } else {
                                            Toast.makeText(context, "Error al guardar.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Llena los campos obligatorios", Toast.LENGTH_SHORT).show()
                                }
                            }, 
                            modifier = Modifier.fillMaxWidth().height(48.dp), 
                            colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion),
                            shape = RoundedCornerShape(12.dp)
                        ) { 
                            Text("Confirmar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddProductField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, keyboardType: KeyboardType = KeyboardType.Text) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ColorVerdeOlivaOscuro)
        OutlinedTextField(
            value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), 
            placeholder = { Text(placeholder, fontSize = 12.sp) }, 
            shape = RoundedCornerShape(12.dp), 
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )
    }
}
