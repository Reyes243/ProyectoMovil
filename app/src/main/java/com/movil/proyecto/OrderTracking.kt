
package com.movil.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
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
import com.movil.proyecto.ui.theme.*

class OrderTracking : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                OrderTrackingScreen(
                    onBack = { finish() },
                    onLogout = { finishAffinity() }
                )
            }
        }
    }
}

@Composable
fun OrderTrackingScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
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
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión", tint = ColorCremaCampos, modifier = Modifier.padding(horizontal = 8.dp).size(24.dp).clickable { onLogout() })
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
                            modifier = Modifier.fillMaxWidth().height(48.dp),
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = ColorCremaCampos
            ) {
                Text(
                    text = "RASTREO DE ENVÍO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ColorCremaCampos)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Orden: #RY1234567", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Línea de Tiempo de Rastreo
                    TrackingStep(title = "Pedido confirmado", date = "01 Oct, 13:45", isDone = true)
                    TrackingLine(isDone = true)
                    TrackingStep(title = "En preparación", date = "02 Oct, 09:12", isDone = true)
                    TrackingLine(isDone = false)
                    TrackingStep(title = "En camino", date = "En curso", isDone = false)
                    TrackingLine(isDone = false)
                    TrackingStep(title = "Entregado", date = "-", isDone = false)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ColorCremaCampos)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Estatus de envió", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ColorVerdeOlivaOscuro)
                    Spacer(modifier = Modifier.height(12.dp))
                    StatusItem("Compra realizada con éxito", "01 Oct, 13:45")
                    StatusItem("En preparación", "Tu pedido está siendo empaquetado con cuidado.\n02 Oct, 09:12")
                    StatusItem("En camino", "Tu pedido ha salido de nuestro vivero.\nPróximamente llegará a tu destino.")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TrackingStep(title: String, date: String, isDone: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(24.dp),
            shape = CircleShape,
            color = if (isDone) ColorVerdeOlivaOscuro else Color.Gray.copy(alpha = 0.3f)
        ) {
            if (isDone) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.padding(4.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if(isDone) Color.Black else Color.Gray)
            Text(date, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun TrackingLine(isDone: Boolean) {
    Box(modifier = Modifier.padding(start = 11.dp).width(2.dp).height(30.dp).background(if(isDone) ColorVerdeOlivaOscuro else Color.Gray.copy(alpha = 0.3f)))
}

@Composable
fun StatusItem(title: String, info: String = "") {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("• $title", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        if (info.isNotEmpty()) {
            Text(info, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(start = 12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderTrackingPreview() {
    ProyectoMovilTheme {
        OrderTrackingScreen(onBack = {}, onLogout = {})
    }
}
