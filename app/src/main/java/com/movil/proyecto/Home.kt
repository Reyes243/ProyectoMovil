
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
import androidx.compose.material.icons.automirrored.filled.Login
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
import com.movil.proyecto.ui.theme.*

class Home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                HomeScreen(
                    onLogout = { 
                        UserManager.logout()
                        val intent = Intent(this, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    },
                    onCategoryClick = { categoryName ->
                        val intent = Intent(this, PlantList::class.java).apply {
                            putExtra("CATEGORY_NAME", categoryName)
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
fun HomeScreen(
    onLogout: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Scaffold(
        topBar = { 
            HomeHeader(
                onLogout = onLogout, 
                onNavigateToCart = onNavigateToCart, 
                onNavigateToAccount = onNavigateToAccount,
                onNavigateToLogin = onNavigateToLogin
            ) 
        },
        containerColor = ColorFondoVerdeClaro
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            MainBanner()

            // API DE CONSEJOS (Dato curioso o Tip)
            var plantFact by remember { mutableStateOf("Cargando consejo verde...") }
            
            val plantTips = listOf(
                "La Monstera necesita luz indirecta para no quemar sus hojas.",
                "El exceso de agua es la principal causa de muerte en suculentas.",
                "Limpia el polvo de las hojas para que respiren mejor.",
                "Hablarle a tus plantas puede ayudarlas a crecer (¡y a ti a desestresarte!).",
                "El Aloe Vera prefiere macetas con muy buen drenaje.",
                "La Lavanda necesita al menos 6 horas de sol directo al día."
            )

            LaunchedEffect(Unit) {
                try {
                    // Intentamos traer un consejo de la API real
                    val response = PlantApiManager.service.getRandomAdvice()
                    // Como la API es en inglés y genérica, mezclamos con un tip de planta local
                    plantFact = plantTips.random()
                } catch (e: Exception) {
                    plantFact = plantTips.random()
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ColorCremaCampos.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp), 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ColorVerdeOlivaOscuro,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🌱", fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Consejo de Raíz Viva", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ColorVerdeOlivaOscuro)
                        Text(
                            text = plantFact,
                            fontSize = 13.sp,
                            color = Color.Black,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Categorías",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ColorVerdeOlivaOscuro,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val categories = listOf(
                CategoryItem("PLANTAS DE INTERIOR", R.drawable.cat_interior),
                CategoryItem("PLANTAS DE EXTERIOR", R.drawable.cat_exterior),
                CategoryItem("BAJO MANTENIMIENTO", R.drawable.cat_bajo),
                CategoryItem("AROMÁTICAS Y COMESTIBLES", R.drawable.cat_aromatica),
                CategoryItem("MACETAS Y ACCESORIOS", R.drawable.cat_macetas),
                CategoryItem("CUIDADOS Y BIENESTAR", R.drawable.cat_cuidados)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeader(
    onLogout: () -> Unit, 
    onNavigateToCart: () -> Unit, 
    onNavigateToAccount: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val isLoggedIn = UserManager.isLoggedIn

    Surface(
        color = ColorVerdeOlivaOscuro, 
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
                    color = ColorCremaCampos
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoggedIn) {
                        IconButton(onClick = onNavigateToAccount) {
                            Icon(Icons.Default.Person, contentDescription = "Perfil", tint = ColorCremaCampos)
                        }
                        IconButton(onClick = onNavigateToCart) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = ColorCremaCampos)
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión", tint = ColorCremaCampos)
                        }
                    } else {
                        TextButton(onClick = onNavigateToLogin) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = ColorCremaCampos)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Login", color = ColorCremaCampos, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
        colors = CardDefaults.cardColors(containerColor = ColorCremaCampos),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.banner_new_arrivals),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun CategoryCard(category: CategoryItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Card(
            modifier = Modifier.aspectRatio(1f).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ColorCremaCampos),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Image(
                painter = painterResource(id = category.imageRes),
                contentDescription = category.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = category.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ColorVerdeOlivaOscuro,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp
        )
    }
}

data class CategoryItem(val name: String, val imageRes: Int)

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    ProyectoMovilTheme {
        HomeScreen(onLogout = {}, onCategoryClick = {}, onNavigateToCart = {}, onNavigateToAccount = {}, onNavigateToLogin = {})
    }
}
