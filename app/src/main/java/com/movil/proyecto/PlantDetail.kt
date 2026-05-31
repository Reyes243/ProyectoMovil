
package com.movil.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    onLogout = { finishAffinity() },
                    onNavigateToCart = {
                        val intent = Intent(this, Cart::class.java)
                        startActivity(intent)
                    }
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
    onNavigateToCart: () -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    val plantInfo = getPlantDetailData(plantName)
    val context = LocalContext.current

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
                            Icon(
                                Icons.Default.ShoppingCart, 
                                contentDescription = "Carrito", 
                                tint = ColorCremaCampos, 
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .size(24.dp)
                                    .clickable { onNavigateToCart() }
                            )
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ColorCremaCampos)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = plantInfo.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Text(
                        text = plantInfo.category,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🌿", fontSize = 100.sp)
                            Surface(
                                color = Color.Gray.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                            ) {
                                Text("1/3", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = plantInfo.price,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Precio incluye IVA.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Text("Stock: ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${plantInfo.stock} Disponibles", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = plantInfo.description,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ColorVerdeMedio,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(44.dp).weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                IconButton(onClick = { if(quantity > 1) quantity-- }) {
                                    Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White)
                                }
                                Text(text = quantity.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                IconButton(onClick = { quantity++ }) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                CartManager.addPlant(plantInfo.name, plantInfo.price, quantity)
                                Toast.makeText(context, "${plantInfo.name} añadido al carrito", Toast.LENGTH_SHORT).show()
                                onNavigateToCart()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(44.dp).weight(1.2f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Agregar al carrito", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TrustInfoItem(Icons.Default.LocalShipping, "Envío especializado")
                        TrustInfoItem(Icons.Default.Spa, "Garantía de 15 días")
                        TrustInfoItem(Icons.Default.Security, "Pago seguro")
                    }
                }
            }
        }
    }
}

data class PlantDetailData(
    val name: String,
    val category: String,
    val price: String,
    val stock: Int,
    val description: String
)

fun getPlantDetailData(name: String): PlantDetailData {
    return when (name) {
        "MONSTERA DELICIOSA" -> PlantDetailData(name, "Planta de interior", "$250.00", 45, "Déjate inspirar por las hojas de la naturaleza con esta Monstera. Se trata de una llamada epifita que se sube a otras plantas para acercarse al sol.")
        "POTO (EPIPREMNUM)" -> PlantDetailData(name, "Planta de interior", "$120.00", 120, "El poto es una de las plantas de interior más resistentes y fáciles de cuidar, perfecta para principiantes. Sus hojas en forma de corazón son icónicas.")
        "CALATHEA ORNATA" -> PlantDetailData(name, "Planta de interior", "$310.00", 25, "Conocida por sus hermosas hojas con rayas rosadas, la Calathea Ornata es una planta elegante que purifica el aire de tu hogar.")
        "FICUS LYRATA" -> PlantDetailData(name, "Planta de interior", "$450.00", 15, "El Ficus Lyrata es el árbol de interior favorito de los diseñadores. Sus grandes hojas en forma de violín aportan un toque escultural.")
        "SANSEVIERIA" -> PlantDetailData(name, "Planta de interior", "$180.00", 80, "También llamada lengua de suegra, es casi indestructible y libera oxígeno durante la noche, ideal para el dormitorio.")
        "ESPATIFILO" -> PlantDetailData(name, "Planta de interior", "$150.00", 60, "El Lirio de la paz es famoso por sus elegantes flores blancas y su gran capacidad para eliminar toxinas del ambiente.")
        
        "LAVANDA" -> PlantDetailData(name, "Planta de exterior", "$80.00", 200, "Famosa por su aroma relajante y sus flores púrpuras. Necesita mucho sol y poco riego una vez establecida.")
        "ROSAL ARBUSTIVO" -> PlantDetailData(name, "Planta de exterior", "$220.00", 40, "Un clásico del jardín que florece durante gran parte del año. Requiere podas regulares y abono constante.")
        "GERANIO" -> PlantDetailData(name, "Planta de exterior", "$60.00", 150, "Plantas muy resistentes que llenan de color los balcones y terrazas. Soportan bien el sol directo.")
        "HORTENSIA" -> PlantDetailData(name, "Planta de exterior", "$280.00", 30, "Destacan por sus grandes pompones de flores. Necesitan mucha humedad y sombra parcial para prosperar.")
        
        "ALOE VERA" -> PlantDetailData(name, "Bajo mantenimiento", "$90.00", 90, "Planta suculenta conocida por sus propiedades medicinales y su facilidad de cuidado. Solo necesita sol y poco riego.")
        "CACTUS DE ASIENTO" -> PlantDetailData(name, "Bajo mantenimiento", "$110.00", 55, "Cactus esférico muy decorativo. Perfecto para lugares con mucha luz donde te olvides de regar.")
        
        "ROMERO" -> PlantDetailData(name, "Aromáticas y comestibles", "$50.00", 110, "Hierba aromática esencial en la cocina mediterránea. Es muy resistente y prefiere suelos bien drenados.")
        "ALBAHACA" -> PlantDetailData(name, "Aromáticas y comestibles", "$40.00", 85, "Planta anual de aroma intenso, perfecta para pastas y ensaladas. Necesita riego regular y mucha luz.")
        
        "MACETA DE BARRO" -> PlantDetailData(name, "Macetas y accesorios", "$120.00", 300, "Maceta clásica que permite que las raíces respiren gracias a su porosidad. Ideal para todo tipo de plantas.")
        "REGADERA VINTAGE" -> PlantDetailData(name, "Macetas y accesorios", "$350.00", 20, "Regadera de metal con diseño retro. Además de útil, sirve como elemento decorativo para tu rincón verde.")
        
        "HUMIDIFICADOR" -> PlantDetailData(name, "Cuidados y bienestar", "$650.00", 12, "Ayuda a mantener la humedad óptima para tus plantas tropicales, evitando que las puntas de las hojas se sequen.")
        "GUÍA BOTÁNICA" -> PlantDetailData(name, "Cuidados y bienestar", "$280.00", 45, "Libro ilustrado con todos los consejos necesarios para que tus plantas crezcan sanas y fuertes.")
        
        else -> PlantDetailData(name, "General", "$100.00", 10, "Producto de alta calidad seleccionado por expertos botánicos para el bienestar de tu hogar.")
    }
}

@Composable
fun TrustInfoItem(icon: ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(90.dp)) {
        Icon(icon, contentDescription = null, tint = ColorVerdeOlivaOscuro, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text, 
            fontSize = 10.sp, 
            textAlign = TextAlign.Center, 
            lineHeight = 12.sp,
            color = ColorVerdeOlivaOscuro,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlantDetailPreview() {
    ProyectoMovilTheme {
        PlantDetailScreen(plantName = "MONSTERA DELICIOSA", onBack = {}, onLogout = {}, onNavigateToCart = {})
    }
}
