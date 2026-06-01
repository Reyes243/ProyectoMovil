
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.proyecto.ui.theme.*

class Checkout : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                CheckoutScreen(
                    onBack = { finish() },
                    onLogout = { finishAffinity() },
                    onConfirm = {
                        Toast.makeText(this, "¡Compra realizada con éxito! Gracias por confiar en Raíz Viva.", Toast.LENGTH_LONG).show()
                        CartManager.clearCart()
                        val intent = Intent(this, Home::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    },
                    onNavigateToAccount = {
                        val intent = Intent(this, Account::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onConfirm: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    val context = LocalContext.current
    
    // Estados para Información de Pago
    var email by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var expiration by remember { mutableStateOf("") }
    var billingAddress by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("México") }
    
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
                            IconButton(onClick = onNavigateToAccount) {
                                Icon(
                                    Icons.Default.Person, 
                                    contentDescription = "Perfil", 
                                    tint = ColorCremaCampos
                                )
                            }
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = ColorCremaCampos, modifier = Modifier.padding(horizontal = 8.dp).size(24.dp))
                            IconButton(onClick = onLogout) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Logout, 
                                    contentDescription = "Cerrar sesión", 
                                    tint = ColorCremaCampos
                                )
                            }
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = ColorCremaCampos
            ) {
                Text(
                    text = "DETALLES DE PAGO",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resumen del pedido con imágenes
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ColorCremaCampos)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Resumen de tu pedido", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(modifier = Modifier.size(50.dp), shape = RoundedCornerShape(8.dp), color = Color.White) {
                                Image(
                                    painter = painterResource(id = item.imageRes),
                                    null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Cant: ${item.quantity}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Text(item.price, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    PaymentSummaryRow("Subtotal:", "$ ${String.format("%.2f", total)}")
                    PaymentSummaryRow("Envío:", "$ 0.00")
                    PaymentSummaryRow("IVA:", "Incluido")
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL:", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("$ ${String.format("%.2f", total)}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información del método de pago
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ColorCremaCampos)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Ingrese los datos de su método de pago", fontSize = 12.sp, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    CheckoutField(label = "Correo", value = email, onValueChange = { email = it }, placeholder = "Tucorreo@ejemplo.com")
                    CheckoutField(label = "Nombre en tarjeta", value = cardName, onValueChange = { cardName = it }, placeholder = "Nombre que aparece en tarjeta")
                    CheckoutField(label = "Número de tarjeta", value = cardNumber, onValueChange = { cardNumber = it }, placeholder = "1111 1111 1111 1111")
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            CheckoutField(label = "CVV", value = cvv, onValueChange = { cvv = it }, placeholder = "• • •")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            CheckoutField(label = "Expiración", value = expiration, onValueChange = { expiration = it }, placeholder = "MM/AA")
                        }
                    }
                    
                    CheckoutField(label = "Dirección de facturación", value = billingAddress, onValueChange = { billingAddress = it }, placeholder = "Calle, numero")
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1.2f)) {
                            CheckoutField(label = "Ciudad", value = city, onValueChange = { city = it }, placeholder = "Ciudad")
                        }
                        Box(modifier = Modifier.weight(0.8f)) {
                            CheckoutField(label = "Codigo Postal", value = postalCode, onValueChange = { postalCode = it }, placeholder = "00000")
                        }
                    }
                    
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("Pais", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = ColorVerdeOlivaOscuro,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
            ) {
                Text("Cancelar", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (email.isBlank() || cardName.isBlank() || cardNumber.isBlank() || cvv.isBlank() || expiration.isBlank() || billingAddress.isBlank() || city.isBlank() || postalCode.isBlank()) {
                        Toast.makeText(context, "Por favor, completa todos los campos de pago", Toast.LENGTH_SHORT).show()
                    } else {
                        onConfirm()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PaymentSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(value, fontSize = 16.sp, color = if(value == "Incluido") Color.Gray else Color.Black)
    }
}

@Composable
fun CheckoutField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 12.sp, color = Color.Gray) },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = ColorVerdeOlivaOscuro,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
            ),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutPreview() {
    ProyectoMovilTheme {
        CheckoutScreen(onBack = {}, onLogout = {}, onConfirm = {}, onNavigateToAccount = {})
    }
}
