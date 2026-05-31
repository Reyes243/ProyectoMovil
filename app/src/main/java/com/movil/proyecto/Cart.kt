
package com.movil.proyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.proyecto.ui.theme.*

class Cart : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                CartScreen(
                    onBack = { finish() },
                    onLogout = { finishAffinity() },
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
    onNavigateToHome: () -> Unit
) {
    val cartItems = CartManager.items
    val total = CartManager.getTotal()

    Scaffold(
        topBar = {
            Surface(color = ColorVerdeOlivaOscuro, shadowElevation = 4.dp) {
                Column(modifier = Modifier.padding(top = 44.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Raíz Viva",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorCremaCampos
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = "Perfil", tint = ColorCremaCampos, modifier = Modifier.padding(horizontal = 8.dp).size(24.dp))
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = ColorCremaCampos, modifier = Modifier.padding(horizontal = 8.dp).size(24.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.Logout, 
                                contentDescription = "Cerrar sesión", 
                                tint = ColorCremaCampos,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .size(24.dp)
                                    .clickable { onLogout() }
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 16.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = ColorCremaCampos)
                        }
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            placeholder = { Text("Buscar plantas...", fontSize = 14.sp, color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ColorCremaCampos,
                                unfocusedContainerColor = ColorCremaCampos,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }
                }
            }
        },
        containerColor = ColorFondoVerdeClaro
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = ColorCremaCampos
            ) {
                Text(
                    text = "CARRITO",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tu carrito está vacío", fontSize = 18.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateToHome, colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeOlivaOscuro)) {
                            Text("Ir a comprar")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(item, onRemove = { CartManager.removePlant(item.name) })
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = { CartManager.clearCart() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                            ) {
                                Text("Vaciar carrito")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = onNavigateToHome,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                elevation = ButtonDefaults.buttonElevation(2.dp)
                            ) {
                                Text("Seguir comprando", color = ColorVerdeOlivaOscuro, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Resumen inferior
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = ColorCremaCampos,
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Productos", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("$ ${String.format("%.2f", total)}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Envío", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Gratis", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text("$ ${String.format("%.2f", total)}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onNavigateToCheckout,
                        enabled = cartItems.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorNaranjaAccion,
                            disabledContainerColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Comprar ahora", fontSize = 24.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItemData, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de miniatura
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🌿", fontSize = 40.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ColorVerdeOlivaOscuro)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Cantidad", fontSize = 11.sp, color = Color.Gray)
                        Surface(color = Color.LightGray, shape = RoundedCornerShape(4.dp)) {
                            Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Precio", fontSize = 11.sp, color = Color.Gray)
                        Text(item.price, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Subtotal", fontSize = 11.sp, color = Color.Gray)
                        val priceValue = item.price.replace("$", "").toDoubleOrNull() ?: 0.0
                        Text("$ ${String.format("%.2f", priceValue * item.quantity)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class CartItemData(val name: String, val price: String, val quantity: Int)

@Preview(showBackground = true)
@Composable
fun CartPreview() {
    ProyectoMovilTheme {
        CartScreen(onBack = {}, onLogout = {}, onNavigateToCheckout = {}, onNavigateToHome = {})
    }
}
