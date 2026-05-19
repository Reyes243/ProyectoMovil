
package com.movil.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.proyecto.ui.theme.*

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                HomeScreen(onLogout = { finish() })
            }
        }
    }
}

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    Scaffold(
        topBar = { HomeHeader(onLogout = onLogout) },
        containerColor = ColorFondoVerdeClaro // #ADD9B3 - El verde de fondo general
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. BANNER PRINCIPAL (Estilo NEW PLANTS ARRIVAL)
            MainBanner()

            Spacer(modifier = Modifier.height(24.dp))

            // 2. TÍTULO DE CATEGORÍAS
            Text(
                text = "Categorías",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ColorVerdeOlivaOscuro, // #657B68
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 3. GRID DE CATEGORÍAS
            val categories = listOf(
                CategoryItem("PLANTAS DE INTERIOR", ""),
                CategoryItem("PLANTAS DE EXTERIOR", ""),
                CategoryItem("BAJO MANTENIMIENTO", ""),
                CategoryItem("AROMÁTICAS Y COMESTIBLES", ""),
                CategoryItem("MACETAS Y ACCESORIOS", ""),
                CategoryItem("CUIDADOS Y BIENESTAR", "")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(category)
                }
            }
        }
    }
}

@Composable
fun HomeHeader(onLogout: () -> Unit) {
    Surface(
        color = ColorVerdeOlivaOscuro, // #657B68 - Fondo oscuro para el header
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Raíz Viva",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorCremaCampos // #F5F1E3 - Letras claras
                )
                Row {
                    IconButton(onClick = { /* Carrito */ }) {
                        Icon(
                            Icons.Default.ShoppingCart, 
                            contentDescription = "Carrito", 
                            tint = ColorCremaCampos
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout, 
                            contentDescription = "Cerrar sesión", 
                            tint = ColorCremaCampos
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de búsqueda adaptada al estilo de la imagen
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar plantas...", fontSize = 14.sp, color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
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

@Composable
fun MainBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCremaCampos), // #F5F1E3
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "02.12.22", 
                    fontSize = 14.sp, 
                    color = ColorVerdeMedio, 
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "NEW PLANTS ARRIVAL",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorVerdeOlivaOscuro,
                    lineHeight = 26.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Ver mas */ },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Check out", fontSize = 14.sp, color = Color.White)
                }
            }

            // Espacio representativo para la imagen de la planta en el banner
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxHeight()

                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("", color = ColorVerdeOlivaOscuro, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoryItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ColorCremaCampos),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Representación de la imagen de la planta
                Text(
                    text = "",
                    fontSize = 48.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = category.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ColorVerdeOlivaOscuro,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

data class CategoryItem(val name: String, val imageRes: String)

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    ProyectoMovilTheme {
        HomeScreen(onLogout = {})
    }
}
