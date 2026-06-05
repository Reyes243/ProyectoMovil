
package com.movil.proyecto

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.movil.proyecto.ui.theme.*

class OrderTicket : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val orderId = intent.getStringExtra("ORDER_ID") ?: ""
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                OrderTicketScreen(
                    orderId = orderId,
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
fun OrderTicketScreen(
    orderId: String,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val order = OrderManager.getOrderById(orderId) ?: OrderManager.orders.firstOrNull()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = ColorCremaCampos) {
                Text("TICKET", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (order != null) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = ColorCremaCampos)) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Raíz Viva", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = ColorVerdeOlivaOscuro)
                        Text("Fecha: ${order.date}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(24.dp))
                        TicketRow("Datos del cliente", UserManager.currentUser?.fullName ?: "Invitado")
                        TicketRow("Pedido #", order.id)
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        order.items.forEach { item ->
                            TicketProductItem(item.name, item.quantity.toString(), item.price)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))
                        TicketRow("Total:", "$ ${String.format("%.2f", order.total)}", isTotal = true)
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { 
                                scope.launch {
                                    PdfManager.generatePurchaseTicket(context, order)
                                }
                            }, 
                            modifier = Modifier.fillMaxWidth(), 
                            colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion)
                        ) { 
                            Text("DESCARGAR TICKET") 
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontró la información del pedido.", color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TicketRow(label: String, value: String, isTotal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, fontWeight = if(isTotal) FontWeight.ExtraBold else FontWeight.Medium)
        Text(value, fontSize = 12.sp, fontWeight = if(isTotal) FontWeight.ExtraBold else FontWeight.Bold, color = if(isTotal) ColorVerdeOlivaOscuro else Color.Black)
    }
}

@Composable
fun TicketProductItem(name: String, qty: String, subtotal: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(name, fontSize = 11.sp, modifier = Modifier.weight(1f))
        Text(qty, fontSize = 11.sp, modifier = Modifier.width(40.dp), textAlign = TextAlign.Center)
        Text(subtotal, fontSize = 11.sp, modifier = Modifier.width(80.dp), textAlign = TextAlign.End)
    }
}

@Preview(showBackground = true)
@Composable
fun OrderTicketPreview() {
    ProyectoMovilTheme { OrderTicketScreen(orderId = "", onBack = {}, onLogout = {}) }
}
