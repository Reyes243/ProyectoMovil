
package com.movil.proyecto

import androidx.compose.runtime.mutableStateListOf

data class CartItemData(val name: String, val price: String, val quantity: Int, val imageRes: Int)

object CartManager {
    private val _items = mutableStateListOf<CartItemData>()
    val items: List<CartItemData> get() = _items

    fun addPlant(name: String, price: String, quantity: Int, imageRes: Int) {
        val existingItem = _items.find { it.name == name }
        if (existingItem != null) {
            val index = _items.indexOf(existingItem)
            _items[index] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            _items.add(CartItemData(name, price, quantity, imageRes))
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
