
package com.movil.proyecto

import androidx.compose.runtime.mutableStateListOf

object ProductManager {
    private val _catalog = mutableStateListOf<PlantItem>()
    val catalog: List<PlantItem> get() = _catalog

    init {
        // Inicializamos con los productos base
        _catalog.addAll(listOf(
            // INTERIOR
            PlantItem("MONSTERA DELICIOSA", "$250.00", R.drawable.monstera_1, "PLANTAS DE INTERIOR"),
            PlantItem("POTO (EPIPREMNUM)", "$120.00", R.drawable.planta_1, "PLANTAS DE INTERIOR"),
            PlantItem("CALATHEA ORNATA", "$310.00", R.drawable.cat_interior, "PLANTAS DE INTERIOR"),
            PlantItem("FICUS LYRATA", "$450.00", R.drawable.planta_2, "PLANTAS DE INTERIOR"),
            
            // EXTERIOR
            PlantItem("LAVANDA", "$80.00", R.drawable.cat_exterior, "PLANTAS DE EXTERIOR"),
            PlantItem("ROSAL ARBUSTIVO", "$220.00", R.drawable.planta_1, "PLANTAS DE EXTERIOR"),
            PlantItem("GERANIO", "$60.00", R.drawable.planta_2, "PLANTAS DE EXTERIOR"),
            
            // BAJO MANTENIMIENTO
            PlantItem("ALOE VERA", "$90.00", R.drawable.cat_bajo, "BAJO MANTENIMIENTO"),
            PlantItem("CACTUS DE ASIENTO", "$110.00", R.drawable.planta_2, "BAJO MANTENIMIENTO"),
            
            // AROMÁTICAS
            PlantItem("ROMERO", "$50.00", R.drawable.cat_aromatica, "AROMÁTICAS Y COMESTIBLES"),
            PlantItem("ALBAHACA", "$40.00", R.drawable.planta_1, "AROMÁTICAS Y COMESTIBLES"),
            
            // ACCESORIOS
            PlantItem("MACETA DE BARRO", "$120.00", R.drawable.cat_macetas, "MACETAS Y ACCESORIOS"),
            PlantItem("REGADERA VINTAGE", "$350.00", R.drawable.planta_1, "MACETAS Y ACCESORIOS"),
            PlantItem("SUSTRATO ORGÁNICO", "$95.00", R.drawable.sustrato, "MACETAS Y ACCESORIOS"),
            
            // CUIDADOS
            PlantItem("HUMIDIFICADOR", "$650.00", R.drawable.cat_cuidados, "CUIDADOS Y BIENESTAR")
        ))
    }

    fun addProduct(name: String, price: String, category: String, imageRes: Int = R.drawable.logo) {
        _catalog.add(PlantItem(name.uppercase(), price, imageRes, category.uppercase(), isUserAdded = true))
    }

    fun deleteProduct(name: String) {
        _catalog.removeAll { it.name == name }
    }

    fun updateProduct(oldName: String, newName: String, newPrice: String, newCategory: String) {
        val index = _catalog.indexOfFirst { it.name == oldName }
        if (index != -1) {
            val oldItem = _catalog[index]
            _catalog[index] = oldItem.copy(
                name = newName.uppercase(), 
                price = newPrice, 
                category = newCategory.uppercase(),
                isUserAdded = true
            )
        }
    }

    fun getUserProducts(): List<PlantItem> {
        return _catalog.filter { it.isUserAdded }
    }

    fun getProductsByCategory(category: String): List<PlantItem> {
        return _catalog.filter { it.category == category.uppercase() }
    }
}
