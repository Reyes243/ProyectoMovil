
package com.movil.proyecto

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.movil.proyecto.db.AppDatabase
import com.movil.proyecto.db.CartEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import kotlinx.coroutines.flow.collect
import java.util.Locale

data class CartItemData(val name: String, val price: String, val quantity: Int, val imageRes: Int)

object CartManager {
    private val _items = mutableStateListOf<CartItemData>()
    val items: List<CartItemData> get() = _items

    private lateinit var db: AppDatabase

    fun init(context: Context) {
        db = AppDatabase.getDatabase(context)
        loadCartFromDb()
    }

    fun loadCartFromDb() {
        val userId = UserManager.currentUser?.id ?: return
        // Por ahora deshabilitamos Room para evitar conflictos de tipos con Firebase
        // Pronto migraremos el carrito también a la nube
        _items.clear()
    }

    fun addPlant(name: String, price: String, quantity: Int, imageRes: Int) {
        val existingItem = _items.find { it.name == name }
        if (existingItem != null) {
            val index = _items.indexOf(existingItem)
            val newQuantity = existingItem.quantity + quantity
            _items[index] = existingItem.copy(quantity = newQuantity)
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

    fun clearCartItemsOnly() {
        _items.clear()
    }



    fun getTotal(): Double {
        return _items.sumOf { 
            val priceValue = it.price.replace("$", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
            priceValue * it.quantity
        }
    }
}

