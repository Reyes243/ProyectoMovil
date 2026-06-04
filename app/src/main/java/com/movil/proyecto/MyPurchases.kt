
package com.movil.proyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.proyecto.ui.theme.*

class MyPurchases : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                MyPurchasesScreen(
                    onBack = { finish() },
                    onLogout = { 
                        UserManager.logout()
                        val intent = Intent(this, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    },
                    onNavigateToTicket = { orderId ->
                        val intent = Intent(this, OrderTicket::class.java).apply {
                            putExtra("ORDER_ID", orderId)
                        }
                        startActivity(intent)
                    },
                    onNavigateToTracking = { orderId ->
                        val intent = Intent(this, OrderTracking::class.java).apply {
                            putExtra("ORDER_ID", orderId)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun MyPurchasesScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToTicket: (String) -> Unit,
    onNavigateToTracking: (String) -> Unit
) {
    val orders = OrderManager.orders

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
                Text("MIS COMPRAS", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No has realizado compras aún.", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
                    items(orders) { order ->
                        PurchaseCard(order, { onNavigateToTicket(order.id) }, { onNavigateToTracking(order.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun PurchaseCard(order: OrderData, onTicket: () -> Unit, onTracking: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = ColorCremaCampos)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(60.dp), shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.5f)) {
                    if (order.items.isNotEmpty()) {
                        Image(painter = painterResource(id = order.items.first().imageRes), null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Box(contentAlignment = Alignment.Center) { Text("🌿", fontSize = 30.sp) }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(order.date, fontSize = 12.sp, color = Color.Gray)
                    Text(if(order.items.size == 1) order.items.first().name else "${order.items.size} Productos", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("ID: ${order.id}", fontSize = 11.sp, color = Color.Gray)
                }
                Text("$ ${String.format("%.2f", order.total)}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = ColorVerdeOlivaOscuro)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Desglose de productos (opcional, ya que "Ver Ticket" da más detalle)
            Column(modifier = Modifier.padding(start = 8.dp)) {
                order.items.forEach { item ->
                    Text("• ${item.name} x${item.quantity}", fontSize = 11.sp, color = Color.DarkGray)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onTracking, modifier = Modifier.weight(1f).height(36.dp), colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeOlivaOscuro), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(0.dp)) {
                    Text("Ver Rastreo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(onClick = onTicket, modifier = Modifier.weight(1f).height(36.dp), colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(0.dp)) {
                    Text("Ver Ticket", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyPurchasesPreview() {
    ProyectoMovilTheme { MyPurchasesScreen(onBack = {}, onLogout = {}, onNavigateToTicket = {}, onNavigateToTracking = {}) }
}
