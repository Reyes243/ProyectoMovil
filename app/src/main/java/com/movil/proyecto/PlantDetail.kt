
package com.movil.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.movil.proyecto.ui.theme.*

class PlantDetail : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val plantName = intent.getStringExtra("PLANT_NAME") ?: "MONSTERA DELICIOSA"
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                PlantDetailScreen(
                    plantName = plantName,
                    onBack = { finish() },
                    onLogout = { 
                        UserManager.logout()
                        val intent = Intent(this, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    },
                    onNavigateToCart = { startActivity(Intent(this, Cart::class.java)) },
                    onNavigateToAccount = { startActivity(Intent(this, Account::class.java)) },
                    onNavigateToLogin = { startActivity(Intent(this, Login::class.java)) }
                )
            }
        }
    }
}

@Composable
fun PlantDetailScreen(
    plantName: String,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val isLoggedIn = UserManager.isLoggedIn
    var quantity by remember { mutableStateOf(1) }
    val context = LocalContext.current
    
    var plantItem by remember { mutableStateOf<PlantItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(plantName) {
        val allProducts = ProductManager.getProductsByCategory("PLANTAS DE INTERIOR") + 
                         ProductManager.getProductsByCategory("PLANTAS DE EXTERIOR") +
                         ProductManager.getProductsByCategory("BAJO MANTENIMIENTO") +
                         ProductManager.getProductsByCategory("AROMÁTICAS Y COMESTIBLES") +
                         ProductManager.getProductsByCategory("MACETAS Y ACCESORIOS") +
                         ProductManager.getProductsByCategory("CUIDADOS Y BIENESTAR")
        
        plantItem = allProducts.find { it.name.uppercase() == plantName.uppercase() }
        isLoading = false
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
                            if (isLoggedIn) {
                                IconButton(onClick = onNavigateToAccount) { Icon(Icons.Default.Person, null, tint = ColorCremaCampos) }
                                IconButton(onClick = onNavigateToCart) { Icon(Icons.Default.ShoppingCart, null, tint = ColorCremaCampos) }
                                IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = ColorCremaCampos) }
                            } else {
                                TextButton(onClick = onNavigateToLogin) {
                                    Text("Login", color = ColorCremaCampos, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 16.dp, bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = ColorCremaCampos) }
                        OutlinedTextField(
                            value = "", onValueChange = {}, placeholder = { Text("Buscar plantas...", fontSize = 14.sp) },
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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ColorVerdeOlivaOscuro)
            }
        } else if (plantItem == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Producto no encontrado")
            }
        } else {
            val item = plantItem!!
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp)) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = ColorCremaCampos)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = item.name, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        Text(text = item.category, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth().height(300.dp), 
                            shape = RoundedCornerShape(16.dp), 
                            color = Color.White
                        ) {
                            if (!item.imageUrl.isNullOrEmpty()) {
                                AsyncImage(model = item.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                val resId = if (item.imageRes != 0) item.imageRes else R.drawable.logo
                                Image(
                                    painter = painterResource(id = resId), 
                                    null, 
                                    modifier = Modifier.fillMaxSize(), 
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = item.price, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(text = "Precio incluye IVA.", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Text("Stock: ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${item.stock} Disponibles", fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = item.description, fontSize = 14.sp, lineHeight = 20.sp, color = Color.DarkGray)

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = ColorVerdeMedio, shape = RoundedCornerShape(8.dp), modifier = Modifier.height(44.dp).weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(horizontal = 8.dp)) {
                                    IconButton(onClick = { if(quantity > 1) quantity-- }) { Icon(Icons.Default.Remove, null, tint = Color.White) }
                                    Text(text = quantity.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    IconButton(onClick = { if(quantity < item.stock) quantity++ }) { Icon(Icons.Default.Add, null, tint = Color.White) }
                                }
                            }

                            Button(
                                onClick = {
                                    if (isLoggedIn) {
                                        CartManager.addPlant(
                                            id = item.id,
                                            name = item.name, 
                                            price = item.price, 
                                            quantity = quantity, 
                                            imageRes = item.imageRes, 
                                            imageUrl = item.imageUrl
                                        )
                                        Toast.makeText(context, "${item.name} añadido al carrito", Toast.LENGTH_SHORT).show()
                                        onNavigateToCart()
                                    } else {
                                        Toast.makeText(context, "Debes iniciar sesión para comprar", Toast.LENGTH_LONG).show()
                                        onNavigateToLogin()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion),
                                shape = RoundedCornerShape(8.dp),
                                enabled = item.stock > 0,
                                modifier = Modifier.height(44.dp).weight(1.2f)
                            ) {
                                Text(if(item.stock > 0) "Agregar al carrito" else "Agotado", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            TrustInfoItem(Icons.Default.LocalShipping, "Envío especializado")
                            TrustInfoItem(Icons.Default.Spa, "Garantía de 15 días")
                            TrustInfoItem(Icons.Default.Security, "Pago seguro")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrustInfoItem(icon: ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(90.dp)) {
        Icon(icon, null, tint = ColorVerdeOlivaOscuro, modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text, fontSize = 10.sp, textAlign = TextAlign.Center, color = ColorVerdeOlivaOscuro, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun PlantDetailPreview() {
    ProyectoMovilTheme {
        PlantDetailScreen(plantName = "MONSTERA DELICIOSA", onBack = {}, onLogout = {}, onNavigateToCart = {}, onNavigateToAccount = {}, onNavigateToLogin = {})
    }
}
