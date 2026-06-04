
package com.movil.proyecto

import androidx.compose.runtime.mutableStateListOf

data class OrderData(
    val id: String,
    val date: String,
    val items: List<CartItemData>,
    val total: Double
)

object OrderManager {
    private val _orders = mutableStateListOf<OrderData>()
    val orders: List<OrderData> get() = _orders

    fun addOrder(items: List<CartItemData>, total: Double) {
        val newOrder = OrderData(
            id = "RY${(1000000..9999999).random()}",
            date = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
            items = items.toList(),
            total = total
        )
        _orders.add(0, newOrder) // Agregamos al inicio para que salgan las más recientes primero
    }

    fun getOrderById(id: String): OrderData? {
        return _orders.find { it.id == id }
    }

    fun clearOrders() {
        _orders.clear()
    }
}
