
package com.movil.proyecto

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.movil.proyecto.db.AppDatabase
import com.movil.proyecto.db.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class UserData(
    val id: Int = 0,
    val fullName: String,
    val email: String,
    val password: String,
    val address: String,
    val phone: String
)

object UserManager {
    var currentUser by mutableStateOf<UserData?>(null)
        private set
    
    val isLoggedIn: Boolean get() = currentUser != null

    private lateinit var db: AppDatabase

    fun init(context: Context) {
        db = AppDatabase.getDatabase(context)
    }

    fun registerUser(user: UserData): Boolean = runBlocking(Dispatchers.IO) {
        val existing = db.userDao().getUserByEmail(user.email)
        if (existing != null) return@runBlocking false
        
        val newUserId = db.userDao().register(
            UserEntity(
                nombre = user.fullName,
                apellidos = "",
                email = user.email,
                password = user.password,
                rol = "cliente",
                direccion = user.address,
                telefono = user.phone
            )
        )
        return@runBlocking newUserId > 0
    }

    fun loginUser(email: String, password: String): UserData? = runBlocking(Dispatchers.IO) {
        val entity = db.userDao().login(email, password)
        if (entity != null) {
            val userData = UserData(
                id = entity.usuario_id,
                fullName = entity.nombre ?: "",
                email = entity.email ?: "",
                password = entity.password ?: "",
                address = entity.direccion ?: "",
                phone = entity.telefono ?: ""
            )
            currentUser = userData
            return@runBlocking userData
        }
        return@runBlocking null
    }

    fun logout() {
        currentUser = null
    }

    fun updateUser(fullName: String, address: String, phone: String) {
        currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                val entity = db.userDao().getUserByEmail(user.email)
                entity?.let {
                    val updatedEntity = it.copy(
                        nombre = fullName,
                        direccion = address,
                        telefono = phone
                    )
                    db.userDao().updateUser(updatedEntity)
                    currentUser = user.copy(fullName = fullName, address = address, phone = phone)
                }
            }
        }
    }
}
