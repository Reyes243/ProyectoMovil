
package com.movil.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.proyecto.ui.theme.*

class AddProduct : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val editMode = intent.getBooleanExtra("EDIT_MODE", false)
        val plantName = intent.getStringExtra("PLANT_NAME") ?: ""
        
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                AddProductScreen(
                    editMode = editMode,
                    existingPlantName = plantName,
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
    existingPlantName: String = "",
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val existingProduct = if (editMode) ProductManager.catalog.find { it.name == existingPlantName } else null

    var name by remember { mutableStateOf(existingProduct?.name ?: "") }
    var price by remember { mutableStateOf(existingProduct?.price ?: "") }
    var description by remember { mutableStateOf("") } // Podría extenderse PlantItem para incluir descripción
    var category by remember { mutableStateOf(existingProduct?.category ?: "PLANTAS DE INTERIOR") }
    var expanded by remember { mutableStateOf(false) }

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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProductImagePlaceholder(Modifier.weight(1f))
                        ProductImagePlaceholder(Modifier.weight(1f))
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

                    AddProductField("Precio", price, { price = it }, "Ej. $ 250.00")
                    
                    AddProductField("Descripción", description, { description = it }, "Detalles del producto...")

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { 
                            if (name.isNotBlank() && price.isNotBlank()) {
                                if (editMode) {
                                    ProductManager.updateProduct(existingPlantName, name, price, category)
                                    Toast.makeText(context, "Producto actualizado", Toast.LENGTH_SHORT).show()
                                } else {
                                    ProductManager.addProduct(name, price, category)
                                    Toast.makeText(context, "Producto agregado", Toast.LENGTH_SHORT).show()
                                }
                                onBack()
                            } else {
                                Toast.makeText(context, "Llena los campos obligatorios", Toast.LENGTH_SHORT).show()
                            }
                        }, 
                        modifier = Modifier.fillMaxWidth(), 
                        colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion)
                    ) { 
                        Text("Confirmar") 
                    }
                }
            }
        }
    }
}

@Composable
fun ProductImagePlaceholder(modifier: Modifier = Modifier) {
    Surface(modifier = modifier.aspectRatio(1f), color = Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))) {
        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.AddAPhoto, null, tint = Color.Gray) }
    }
}

@Composable
fun AddProductField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ColorVerdeOlivaOscuro)
        OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), placeholder = { Text(placeholder, fontSize = 12.sp) }, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
    }
}

@Preview(showBackground = true)
@Composable
fun AddProductPreview() {
    ProyectoMovilTheme { AddProductScreen(onBack = {}, onLogout = {}) }
}
