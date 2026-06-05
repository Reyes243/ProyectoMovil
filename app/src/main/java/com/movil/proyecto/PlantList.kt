
package com.movil.proyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import coil.compose.AsyncImage
import com.movil.proyecto.ui.theme.*

class PlantList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "PLANTAS"
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                PlantListScreen(
                    categoryName = categoryName,
                    onBack = { finish() },
                    onLogout = { 
                        UserManager.logout()
                        val intent = Intent(this, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    },
                    onPlantClick = { plantId ->
                        val intent = Intent(this, PlantDetail::class.java).apply {
                            putExtra("PLANT_ID", plantId)
                        }
                        startActivity(intent)
                    },
                    onNavigateToCart = {
                        val intent = Intent(this, Cart::class.java)
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
fun PlantListScreen(
    categoryName: String,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onPlantClick: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val isLoggedIn = UserManager.isLoggedIn
    var plants by remember { mutableStateOf(emptyList<PlantItem>()) }

    LaunchedEffect(categoryName) {
        plants = ProductManager.getProductsByCategory(categoryName)
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(text = categoryName, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = ColorVerdeOlivaOscuro)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterBox(label = "Precio mínimo", modifier = Modifier.weight(1f))
                        FilterBox(label = "Precio máximo", modifier = Modifier.weight(1f))
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion), shape = RoundedCornerShape(8.dp), modifier = Modifier.height(36.dp)) {
                            Text("Aplicar", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            items(plants) { plant ->
                PlantCard(plant = plant, onDetailClick = { onPlantClick(plant.id) })
            }
        }
    }
}

@Composable
fun FilterBox(label: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.height(36.dp), shape = RoundedCornerShape(8.dp), color = ColorCremaCampos) {
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PlantCard(plant: PlantItem, onDetailClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ColorCremaCampos), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Surface(modifier = Modifier.fillMaxWidth().aspectRatio(1f), shape = RoundedCornerShape(12.dp), color = Color.White) {
                if (!plant.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = plant.imageUrl,
                        contentDescription = plant.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = if (plant.imageRes != 0) plant.imageRes else R.drawable.logo),
                        contentDescription = plant.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = plant.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 13.sp)
            Text(text = plant.price, fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = onDetailClick, colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().height(32.dp), contentPadding = PaddingValues(0.dp)) {
                Text("MÁS INFORMACIÓN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

data class PlantItem(
    val name: String, 
    val price: String, 
    val imageRes: Int = 0, 
    val imageUrl: String? = null,
    val category: String = "",
    val isUserAdded: Boolean = false,
    var salesCount: Int = 0,
    val stock: Int = 10,
    val description: String = "Producto de alta calidad para el bienestar de tu hogar.",
    val id: String = ""
)

@Preview(showBackground = true)
@Composable
fun PlantListPreview() {
    ProyectoMovilTheme {
        PlantListScreen(categoryName = "PLANTAS DE INTERIOR", onBack = {}, onLogout = {}, onPlantClick = {}, onNavigateToCart = {}, onNavigateToAccount = {}, onNavigateToLogin = {})
    }
}
