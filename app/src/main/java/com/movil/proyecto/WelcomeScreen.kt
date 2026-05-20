package com.movil.proyecto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.proyecto.ui.theme.ColorCremaCampos
import com.movil.proyecto.ui.theme.ColorFondoVerdeClaro
import com.movil.proyecto.ui.theme.ColorNaranjaAccion
import com.movil.proyecto.ui.theme.ColorVerdeOlivaOscuro
import com.movil.proyecto.ui.theme.ProyectoMovilTheme

@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorFondoVerdeClaro) // Mantiene la armonía exterior verde claro
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // =========================================================================
        // ESPACIO PARA TU IMAGEN / LOGOTIPO
        // Cuando tengas tu imagen lista en res/drawable, cambia este Box por:
        // Image(painter = painterResource(id = R.drawable.tu_imagen), contentDescription = "Logo")
        // =========================================================================
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(ColorVerdeOlivaOscuro.copy(alpha = 0.15f), shape = RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "",
                color = ColorVerdeOlivaOscuro,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta contenedora Oliva Oscuro (Idéntica al Login/Registro)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = ColorVerdeOlivaOscuro),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Raíz Viva",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = ColorCremaCampos
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Presiona el botón para ir a la pantalla de inicio de sesión.",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = ColorCremaCampos.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón Naranja de Acción (#E07A5F)
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "INGRESAR",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    ProyectoMovilTheme {
        WelcomeScreen(onNavigateToLogin = {})
    }
}