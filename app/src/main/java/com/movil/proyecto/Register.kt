
package com.movil.proyecto

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movil.proyecto.ui.theme.ColorCremaCampos
import com.movil.proyecto.ui.theme.ColorFondoVerdeClaro
import com.movil.proyecto.ui.theme.ColorNaranjaAccion
import com.movil.proyecto.ui.theme.ColorVerdeOlivaOscuro
import com.movil.proyecto.ui.theme.ProyectoMovilTheme

class Register : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterScreen(
                        onBack = { finish() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorFondoVerdeClaro)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                    text = "Regístrate",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorCremaCampos
                )

                Text(
                    text = "Crea tu cuenta de Raíz Viva",
                    fontSize = 14.sp,
                    color = ColorCremaCampos.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                RegisterField("Nombre Completo", fullName, { fullName = it })
                RegisterField("Correo Electrónico", email, { email = it }, KeyboardType.Email)
                RegisterField("Dirección", address, { address = it })
                RegisterField("Teléfono", phone, { 
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) phone = it 
                }, KeyboardType.Phone)
                RegisterField("Contraseña", password, { password = it }, KeyboardType.Password, isPassword = true)
                RegisterField("Confirmar Contraseña", confirmPassword, { confirmPassword = it }, KeyboardType.Password, isPassword = true)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                        if (fullName.length < 3) {
                            Toast.makeText(context, "El nombre debe ser más largo", Toast.LENGTH_SHORT).show()
                        } else if (!email.matches(emailPattern.toRegex())) {
                            Toast.makeText(context, "Correo inválido", Toast.LENGTH_SHORT).show()
                        } else if (address.isBlank()) {
                            Toast.makeText(context, "La dirección es obligatoria", Toast.LENGTH_SHORT).show()
                        } else if (phone.length != 10 || !phone.all { it.isDigit() }) {
                            Toast.makeText(context, "Teléfono inválido (debe tener 10 números)", Toast.LENGTH_SHORT).show()
                        } else if (password.length < 6) {
                            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                        } else if (password != confirmPassword) {
                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        } else {
                            val success = UserManager.registerUser(UserData(fullName, email, password, address, phone))
                            if (success) {
                                Toast.makeText(context, "¡Registro Exitoso! Inicia sesión", Toast.LENGTH_LONG).show()
                                onBack()
                            } else {
                                Toast.makeText(context, "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaAccion),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("REGISTRARSE", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onBack) {
                    Text("Cancelar", color = ColorCremaCampos, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun RegisterField(label: String, value: String, onValueChange: (String) -> Unit, keyboardType: KeyboardType = KeyboardType.Text, isPassword: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = ColorCremaCampos, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ColorCremaCampos,
                unfocusedContainerColor = ColorCremaCampos,
                focusedTextColor = Color(0xFF2C3530),
                unfocusedTextColor = Color(0xFF2C3530),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    ProyectoMovilTheme {
        RegisterScreen(onBack = {})
    }
}
