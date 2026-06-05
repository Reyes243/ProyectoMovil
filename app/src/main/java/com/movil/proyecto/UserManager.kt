
package com.movil.proyecto

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class UserData(
    val id: String = "", 
    val fullName: String = "",
    val email: String = "",
    val address: String = "",
    val phone: String = ""
)

object UserManager {
    var currentUser by mutableStateOf<UserData?>(null)
        private set
    
    val isLoggedIn: Boolean get() = currentUser != null

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    fun init(context: Context) {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            loadUserData(firebaseUser.uid)
        }
    }

    private fun loadUserData(uid: String) {
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    currentUser = UserData(
                        id = uid,
                        fullName = doc.getString("nombre") ?: "",
                        email = doc.getString("email") ?: "",
                        address = doc.getString("direccion") ?: "",
                        phone = doc.getString("telefono") ?: ""
                    )
                    OrderManager.loadOrders()
                }
            }
    }

    suspend fun registerUser(user: UserData, password: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.createUserWithEmailAndPassword(user.email, password).await()
                val uid = result.user?.uid ?: return@withContext "Error al obtener UID"

                val userMap = hashMapOf(
                    "nombre" to user.fullName,
                    "email" to user.email,
                    "direccion" to user.address,
                    "telefono" to user.phone,
                    "rol" to "cliente"
                )
                db.collection("usuarios").document(uid).set(userMap).await()
                
                null 
            } catch (e: Exception) {
                e.message ?: "Error desconocido en el registro"
            }
        }
    }

    suspend fun loginUser(email: String, password: String): UserData? {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: return@withContext null
                
                val doc = db.collection("usuarios").document(uid).get().await()
                if (doc.exists()) {
                    val userData = UserData(
                        id = uid,
                        fullName = doc.getString("nombre") ?: "",
                        email = doc.getString("email") ?: "",
                        address = doc.getString("direccion") ?: "",
                        phone = doc.getString("telefono") ?: ""
                    )
                    withContext(Dispatchers.Main) {
                        currentUser = userData
                        OrderManager.loadOrders()
                    }
                    userData
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun logout() {
        auth.signOut()
        currentUser = null
        CartManager.clearCart()
        OrderManager.clearOrders()
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid).delete()
            .addOnSuccessListener {
                auth.currentUser?.delete()?.addOnSuccessListener {
                    logout()
                    onSuccess()
                }
            }
    }

    fun updateUser(fullName: String, address: String, phone: String) {
        val uid = auth.currentUser?.uid ?: return
        val updates = hashMapOf<String, Any>(
            "nombre" to fullName,
            "direccion" to address,
            "telefono" to phone
        )
        db.collection("usuarios").document(uid).update(updates)
            .addOnSuccessListener {
                currentUser = currentUser?.copy(fullName = fullName, address = address, phone = phone)
            }
    }
}
