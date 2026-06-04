
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
import androidx.compose.material.icons.filled.ArrowDropDown
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

class Reports : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                ReportsScreen(
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
fun ReportsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var startDate by remember { mutableStateOf("01/05/2026") }
    var endDate by remember { mutableStateOf("01/06/2026") }
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
                        OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Buscar plantas...") }, leadingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(24.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = ColorCremaCampos, unfocusedContainerColor = ColorCremaCampos))
                    }
                }
            }
        },
        containerColor = ColorFondoVerdeClaro
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = ColorCremaCampos) {
                Text("REPORTES", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Tabs
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReportTabButton("Ventas", selectedTab == 0, modifier = Modifier.weight(1f)) { selectedTab = 0 }
                ReportTabButton("Productos más vendidos", selectedTab == 1, modifier = Modifier.weight(1f)) { selectedTab = 1 }
                ReportTabButton("Clientes con más compras", selectedTab == 2, modifier = Modifier.weight(1f)) { selectedTab = 2 }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filtros de Periodo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ColorCremaCampos)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Periodo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ColorVerdeOlivaOscuro)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ReportPeriodField("Inicio", startDate) { startDate = it }
                    ReportPeriodField("Fin", endDate) { endDate = it }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Confirmar", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {}, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeOlivaOscuro), shape = RoundedCornerShape(8.dp)) {
                            Text("CSV")
                        }
                        Button(onClick = {}, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeOlivaOscuro), shape = RoundedCornerShape(8.dp)) {
                            Text("PDF")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabla de Reporte
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ColorCremaCampos)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        TableHeadItem("Fecha", Modifier.weight(1.5f))
                        TableHeadItem("Folio", Modifier.weight(0.8f))
                        TableHeadItem("Cliente", Modifier.weight(1f))
                        TableHeadItem("Total", Modifier.weight(1f))
                    }
                    HorizontalDivider(color = ColorVerdeOlivaOscuro.copy(alpha = 0.2f))
                    
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(orders) { order ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                TableCellItem(order.date.split(" ").first(), Modifier.weight(1.5f))
                                TableCellItem(order.id.takeLast(4), Modifier.weight(0.8f))
                                TableCellItem(UserManager.currentUser?.fullName?.split(" ")?.first() ?: "Cliente", Modifier.weight(1f))
                                TableCellItem("$ ${String.format("%.2f", order.total)}", Modifier.weight(1f), isBold = true)
                            }
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = ColorFondoVerdeClaro.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total del periodo", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("$ ${String.format("%.2f", orders.sumOf { it.total })}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReportTabButton(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = if (selected) ColorVerdeOlivaOscuro else Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable { onClick() }.height(44.dp),
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
            Text(
                text = text,
                fontSize = 9.sp,
                color = if (selected) Color.White else Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 11.sp
            )
        }
    }
}

@Composable
fun ReportPeriodField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(42.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
        )
    }
}

@Composable
fun TableHeadItem(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier = modifier, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorVerdeOlivaOscuro, textAlign = TextAlign.Center)
}

@Composable
fun TableCellItem(text: String, modifier: Modifier = Modifier, isBold: Boolean = false) {
    Text(text, modifier = modifier, fontSize = 10.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center, maxLines = 1)
}

@Preview(showBackground = true)
@Composable
fun ReportsPreview() {
    ProyectoMovilTheme { ReportsScreen(onBack = {}, onLogout = {}) }
}
