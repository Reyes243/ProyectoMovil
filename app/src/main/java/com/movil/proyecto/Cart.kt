
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

class Cart : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                CartScreen(
                    onBack = { finish() },
                    onLogout = { 
                        UserManager.logout()
                        finish()
                    },
                    onNavigateToCheckout = {
                        if (CartManager.items.isNotEmpty()) {
                            val intent = Intent(this, Checkout::class.java)
                            startActivity(intent)
                        }
                    },
                    onNavigateToHome = {
                        val intent = Intent(this, Home::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    },
                    onNavigateToAccount = {
                        val intent = Intent(this, Account::class.java)
                        startActivity(intent)
                    },
                    onNavigateToLogin = {
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun CartScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val isLoggedIn = UserManager.isLoggedIn
    val cartItems = CartManager.items
    val total = CartManager.getTotal()
    val context = LocalContext.current

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
                                Icon(Icons.Default.ShoppingCart, null, tint = ColorCremaCampos, modifier = Modifier.padding(horizontal = 8.dp).size(24.dp))
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
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = ColorCremaCampos) {
                Text("CARRITO", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tu carrito está vacío", color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateToHome, colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeOlivaOscuro)) { Text("Ir a comprar") }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(cartItems) { item -> CartItemRow(item, onRemove = { CartManager.removePlant(item.name) }) }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            OutlinedButton(onClick = { CartManager.clearCart() }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) { Text("Vaciar") }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = onNavigateToHome, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) { Text("Seguir", color = ColorVerdeOlivaOscuro) }
                        }
                    }
                }
            }

            Surface(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), color = ColorCremaCampos, shape = RoundedCornerShape(24.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Productos", fontWeight = FontWeight.Bold)
                        Text("$ ${String.format("%.2f", total)}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Envío", fontWeight = FontWeight.Bold)
                        Text("Gratis", color = Color.Gray)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("$ ${String.format("%.2f", total)}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (isLoggedIn) {
                                onNavigateToCheckout()
                            } else {
                                Toast.makeText(context, "Debes iniciar sesión para comprar", Toast.LENGTH_LONG).show()
                                onNavigateToLogin()
                            }
                        }, 
                        enabled = cartItems.isNotEmpty(), 
                        modifier = Modifier.fillMaxWidth().height(56.dp), 
                        colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion), 
                        shape = RoundedCornerShape(28.dp)
                    ) { 
                        Text("Comprar ahora", fontSize = 20.sp) 
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItemData, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(60.dp), shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.5f)) { 
                if (!item.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = if (item.imageRes != 0) item.imageRes else R.drawable.logo), 
                        null, 
                        modifier = Modifier.fillMaxSize(), 
                        contentScale = ContentScale.Crop
                    ) 
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Cant: ${item.quantity}", fontSize = 12.sp)
                    Text(item.price, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartPreview() {
    ProyectoMovilTheme { CartScreen(onBack = {}, onLogout = {}, onNavigateToCheckout = {}, onNavigateToHome = {}, onNavigateToAccount = {}, onNavigateToLogin = {}) }
}
