
package com.movil.proyecto

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.movil.proyecto.db.AppDatabase
import com.movil.proyecto.db.OrderDetailEntity
import com.movil.proyecto.db.OrderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class OrderData(
    val id: String,
    val date: String,
    val items: List<CartItemData>,
    val total: Double
)

object OrderManager {
    private val _orders = mutableStateListOf<OrderData>()
    val orders: List<OrderData> get() = _orders

    private lateinit var db: AppDatabase

    fun init(context: Context) {
        db = AppDatabase.getDatabase(context)
        
        CoroutineScope(Dispatchers.IO).launch {
            UserManager.currentUser?.let { user ->
                db.orderDao().getUserOrders(user.id).collectLatest { entities ->
                    val domainOrders = entities.map { entity ->
                        val details = db.orderDao().getOrderDetails(entity.compra_id)
                        OrderData(
                            id = entity.compra_id.toString(),
                            date = entity.fecha,
                            items = details.map { 
                                CartItemData("Producto #${it.producto_producto_id}", "$ ${it.precio_unitario}", it.cantidad ?: 0, R.drawable.logo)
                            },
                            total = entity.total ?: 0.0
                        )
                    }
                    _orders.clear()
                    _orders.addAll(domainOrders)
                }
            }
        }
    }

    fun addOrder(items: List<CartItemData>, total: Double) = runBlocking(Dispatchers.IO) {
        val dateStr = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        
        val orderId = db.orderDao().insertOrder(
            OrderEntity(
                fecha = dateStr,
                total = total,
                nombre_envio = UserManager.currentUser?.fullName ?: "",
                direccion_envio = UserManager.currentUser?.address ?: "",
                ciudad_envio = "La Paz",
                telefono_envio = UserManager.currentUser?.phone ?: "",
                usuario_usuario_id = UserManager.currentUser?.id
            )
        )

        val details = items.map { item ->
            val product = db.productDao().getProductByName(item.name)
            OrderDetailEntity(
                cantidad = item.quantity,
                precio_unitario = item.price.replace("$", "").trim().toDoubleOrNull() ?: 0.0,
                subtotal = (item.price.replace("$", "").trim().toDoubleOrNull() ?: 0.0) * item.quantity,
                compra_compra_id = orderId.toInt(),
                producto_producto_id = product?.producto_id
            )
        }
        db.orderDao().insertOrderDetails(details)
    }

    fun getOrderById(id: String): OrderData? {
        return _orders.find { it.id == id }
    }

    fun clearOrders() {
        _orders.clear()
    }
}
