
package com.movil.proyecto

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class OrderData(
    val id: String,
    val date: String,
    val items: List<CartItemData>,
    val total: Double
)

object OrderManager {
    private val _orders = mutableStateListOf<OrderData>()
    val orders: List<OrderData> get() = _orders

    private val db by lazy { FirebaseFirestore.getInstance() }

    fun init(context: Context) {
        loadOrders()
    }

    fun loadOrders() {
        val user = UserManager.currentUser ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("compras")
                    .whereEqualTo("usuario_id", user.id)
                    .get()
                    .await()
                
                val domainOrders = result.documents.map { doc ->
                    val itemsList = doc.get("items") as? List<Map<String, Any>> ?: emptyList()
                    OrderData(
                        id = doc.id,
                        date = doc.getString("fecha") ?: "",
                        items = itemsList.map { 
                            CartItemData(
                                name = it["nombre"] as? String ?: "",
                                price = it["precio"] as? String ?: "",
                                quantity = (it["cantidad"] as? Long)?.toInt() ?: 0,
                                imageRes = R.drawable.logo
                            )
                        },
                        total = doc.getDouble("total") ?: 0.0
                    )
                }.sortedByDescending { it.date } // Ordenamos en memoria para evitar el índice por ahora

                withContext(Dispatchers.Main) {
                    _orders.clear()
                    _orders.addAll(domainOrders)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addOrder(items: List<CartItemData>, total: Double) = withContext(Dispatchers.IO) {
        val user = UserManager.currentUser ?: return@withContext
        val dateStr = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        
        val orderMap = hashMapOf(
            "fecha" to dateStr,
            "total" to total,
            "usuario_id" to user.id,
            "nombre_envio" to user.fullName,
            "direccion_envio" to user.address,
            "telefono_envio" to user.phone,
            "items" to items.map { 
                mapOf(
                    "nombre" to it.name,
                    "precio" to it.price,
                    "cantidad" to it.quantity
                )
            }
        )

        try {
            db.collection("compras").add(orderMap).await()
            loadOrders() // Recargar para ver la nueva compra
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getOrderById(id: String): OrderData? {
        return _orders.find { it.id == id }
    }

    fun clearOrders() {
        _orders.clear()
    }
}
