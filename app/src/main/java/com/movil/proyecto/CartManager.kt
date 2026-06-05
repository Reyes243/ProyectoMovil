
package com.movil.proyecto

import android.content.Context
import androidx.compose.runtime.mutableStateListOf

data class CartItemData(
    val id: String = "", // Agregamos ID para identificar el producto exacto en DB
    val name: String, 
    val price: String, 
    val quantity: Int, 
    val imageRes: Int, 
    val imageUrl: String? = null
)

object CartManager {
    private val _items = mutableStateListOf<CartItemData>()
    val items: List<CartItemData> get() = _items

    fun init(context: Context) {
        _items.clear()
    }

    fun addPlant(id: String, name: String, price: String, quantity: Int, imageRes: Int, imageUrl: String? = null) {
        val existingItem = _items.find { it.name == name }
        if (existingItem != null) {
            val index = _items.indexOf(existingItem)
            val newQuantity = existingItem.quantity + quantity
            _items[index] = existingItem.copy(quantity = newQuantity)
        } else {
            _items.add(CartItemData(id, name, price, quantity, imageRes, imageUrl))
        }
    }

    fun removePlant(name: String) {
        _items.removeAll { it.name == name }
    }

    fun clearCart() {
        _items.clear()
    }

    fun getTotal(): Double {
        return _items.sumOf { 
            val priceValue = it.price.replace("$", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
            priceValue * it.quantity
        }
    }
}
