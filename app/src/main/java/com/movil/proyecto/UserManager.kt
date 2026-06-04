
package com.movil.proyecto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class UserData(
    val fullName: String,
    val email: String,
    val password: String,
    val address: String,
    val phone: String
)

object UserManager {
    private val _users = mutableStateListOf<UserData>()
    var currentUser by mutableStateOf<UserData?>(null)
        private set
    
    val isLoggedIn: Boolean get() = currentUser != null

    init {
        // Usuario por defecto para pruebas
        _users.add(UserData("Usuario Prueba", "test@gmail.com", "123456", "Calle Principal #123", "6121234567"))
    }

    fun registerUser(user: UserData): Boolean {
        if (_users.any { it.email == user.email }) {
            return false
        }
        _users.add(user)
        return true
    }

    fun loginUser(email: String, password: String): UserData? {
        val user = _users.find { it.email == email && it.password == password }
        if (user != null) {
            currentUser = user
        }
        return user
    }

    fun logout() {
        currentUser = null
    }

    fun updateUser(fullName: String, address: String, phone: String) {
        currentUser?.let { user ->
            val index = _users.indexOfFirst { it.email == user.email }
            if (index != -1) {
                val updatedUser = user.copy(fullName = fullName, address = address, phone = phone)
                _users[index] = updatedUser
                currentUser = updatedUser
            }
        }
    }
}
