
package com.movil.proyecto

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.movil.proyecto.db.AppDatabase
import com.movil.proyecto.db.CategoryEntity
import com.movil.proyecto.db.ProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object ProductManager {
    private val _catalog = mutableStateListOf<PlantItem>()
    val catalog: List<PlantItem> get() = _catalog

    private lateinit var db: AppDatabase

    fun init(context: Context) {
        db = AppDatabase.getDatabase(context)
        
        CoroutineScope(Dispatchers.IO).launch {
            val categories = db.categoryDao().getAllCategories()
            if (categories.isEmpty()) {
                db.categoryDao().insertCategories(listOf(
                    CategoryEntity(1, "PLANTAS DE INTERIOR", "Plantas para dentro de casa"),
                    CategoryEntity(2, "PLANTAS DE EXTERIOR", "Plantas para jardín"),
                    CategoryEntity(3, "BAJO MANTENIMIENTO", "Fáciles de cuidar"),
                    CategoryEntity(4, "AROMÁTICAS Y COMESTIBLES", "Para cocina"),
                    CategoryEntity(5, "MACETAS Y ACCESORIOS", "Complementos"),
                    CategoryEntity(6, "CUIDADOS Y BIENESTAR", "Salud vegetal")
                ))
            }
        }
    }

    fun addProduct(name: String, price: String, category: String, imageRes: Int = R.drawable.logo) = runBlocking(Dispatchers.IO) {
        val priceValue = price.replace("$", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
        val catId = getCategoryId(category)
        
        db.productDao().insertProduct(
            ProductEntity(
                nombre = name.uppercase(),
                descripcion = "Producto agregado por usuario",
                precio = priceValue,
                stock = 10,
                usuario_id = UserManager.currentUser?.id ?: 1,
                imagen = null,
                imagen_extra1 = null,
                imagen_extra2 = null,
                estado = "activo",
                categoria_categoria_id = catId,
                vendedor_id = UserManager.currentUser?.id ?: 1
            )
        )
    }

    private fun getCategoryId(name: String): Int {
        return when (name.uppercase()) {
            "PLANTAS DE INTERIOR" -> 1
            "PLANTAS DE EXTERIOR" -> 2
            "BAJO MANTENIMIENTO" -> 3
            "AROMÁTICAS Y COMESTIBLES" -> 4
            "MACETAS Y ACCESORIOS" -> 5
            "CUIDADOS Y BIENESTAR" -> 6
            else -> 1
        }
    }

    fun deleteProduct(name: String) = runBlocking(Dispatchers.IO) {
        val product = db.productDao().getProductByName(name)
        product?.let { db.productDao().deleteProduct(it) }
    }

    fun updateProduct(oldName: String, newName: String, newPrice: String, newCategory: String) = runBlocking(Dispatchers.IO) {
        val product = db.productDao().getProductByName(oldName)
        product?.let {
            val priceValue = newPrice.replace("$", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
            db.productDao().updateProduct(it.copy(
                nombre = newName.uppercase(),
                precio = priceValue,
                categoria_categoria_id = getCategoryId(newCategory)
            ))
        }
    }

    fun getUserProducts(): List<PlantItem> = runBlocking(Dispatchers.IO) {
        val userId = UserManager.currentUser?.id ?: return@runBlocking emptyList<PlantItem>()
        val entities = db.productDao().getUserProducts(userId).first()
        return@runBlocking entities.map { 
            PlantItem(it.nombre ?: "", "$ ${String.format("%.2f", it.precio)}", R.drawable.logo, getCategoryName(it.categoria_categoria_id), true)
        }
    }

    private fun getCategoryName(id: Int?): String {
        return when (id) {
            1 -> "PLANTAS DE INTERIOR"
            2 -> "PLANTAS DE EXTERIOR"
            3 -> "BAJO MANTENIMIENTO"
            4 -> "AROMÁTICAS Y COMESTIBLES"
            5 -> "MACETAS Y ACCESORIOS"
            6 -> "CUIDADOS Y BIENESTAR"
            else -> "GENERAL"
        }
    }

    fun getProductsByCategory(category: String): List<PlantItem> = runBlocking(Dispatchers.IO) {
        val entities = db.productDao().getProductsByCategory(category).first()
        val localList = entities.map { 
            PlantItem(it.nombre ?: "", "$ ${String.format("%.2f", it.precio)}", R.drawable.logo, category, it.vendedor_id != null)
        }
        
        return@runBlocking getHardcodedProducts(category) + localList
    }

    private fun getHardcodedProducts(category: String): List<PlantItem> {
        return when (category.uppercase()) {
            "PLANTAS DE INTERIOR" -> listOf(
                PlantItem("MONSTERA DELICIOSA", "$250.00", R.drawable.monstera_1, "PLANTAS DE INTERIOR"),
                PlantItem("POTO (EPIPREMNUM)", "$120.00", R.drawable.planta_1, "PLANTAS DE INTERIOR"),
                PlantItem("CALATHEA ORNATA", "$310.00", R.drawable.cat_interior, "PLANTAS DE INTERIOR"),
                PlantItem("FICUS LYRATA", "$450.00", R.drawable.planta_2, "PLANTAS DE INTERIOR"),
                PlantItem("SANSEVIERIA", "$180.00", R.drawable.cat_interior, "PLANTAS DE INTERIOR"),
                PlantItem("ESPATIFILO", "$150.00", R.drawable.planta_1, "PLANTAS DE INTERIOR")
            )
            "PLANTAS DE EXTERIOR" -> listOf(
                PlantItem("LAVANDA", "$80.00", R.drawable.cat_exterior, "PLANTAS DE EXTERIOR"),
                PlantItem("ROSAL ARBUSTIVO", "$220.00", R.drawable.planta_1, "PLANTAS DE EXTERIOR"),
                PlantItem("GERANIO", "$60.00", R.drawable.planta_2, "PLANTAS DE EXTERIOR"),
                PlantItem("HORTENSIA", "$280.00", R.drawable.cat_exterior, "PLANTAS DE EXTERIOR"),
                PlantItem("OLIVO PEQUEÑO", "$550.00", R.drawable.planta_1, "PLANTAS DE EXTERIOR"),
                PlantItem("JAZMÍN", "$140.00", R.drawable.cat_exterior, "PLANTAS DE EXTERIOR")
            )
            "BAJO MANTENIMIENTO" -> listOf(
                PlantItem("ALOE VERA", "$90.00", R.drawable.cat_bajo, "BAJO MANTENIMIENTO"),
                PlantItem("CACTUS DE ASIENTO", "$110.00", R.drawable.planta_2, "BAJO MANTENIMIENTO"),
                PlantItem("SUCULENTA MIX", "$45.00", R.drawable.planta_1, "BAJO MANTENIMIENTO"),
                PlantItem("LENGUA DE SUEGRA", "$170.00", R.drawable.cat_bajo, "BAJO MANTENIMIENTO"),
                PlantItem("ÁRBOL DE JADE", "$200.00", R.drawable.planta_2, "BAJO MANTENIMIENTO"),
                PlantItem("ZAMIOCULCA", "$320.00", R.drawable.cat_bajo, "BAJO MANTENIMIENTO")
            )
            "AROMÁTICAS Y COMESTIBLES" -> listOf(
                PlantItem("ROMERO", "$50.00", R.drawable.cat_aromatica, "AROMÁTICAS Y COMESTIBLES"),
                PlantItem("ALBAHACA", "$40.00", R.drawable.planta_1, "AROMÁTICAS Y COMESTIBLES"),
                PlantItem("MENTA", "$45.00", R.drawable.planta_2, "AROMÁTICAS Y COMESTIBLES"),
                PlantItem("PEREJIL", "$35.00", R.drawable.cat_aromatica, "AROMÁTICAS Y COMESTIBLES"),
                PlantItem("TOMILLO", "$45.00", R.drawable.planta_1, "AROMÁTICAS Y COMESTIBLES"),
                PlantItem("ORÉGANO", "$40.00", R.drawable.cat_aromatica, "AROMÁTICAS Y COMESTIBLES")
            )
            "MACETAS Y ACCESORIOS" -> listOf(
                PlantItem("MACETA DE BARRO", "$120.00", R.drawable.cat_macetas, "MACETAS Y ACCESORIOS"),
                PlantItem("REGADERA VINTAGE", "$350.00", R.drawable.planta_1, "MACETAS Y ACCESORIOS"),
                PlantItem("SUSTRATO ORGÁNICO", "$95.00", R.drawable.sustrato, "MACETAS Y ACCESORIOS"),
                PlantItem("PALA DE MANO", "$85.00", R.drawable.cat_macetas, "MACETAS Y ACCESORIOS"),
                PlantItem("TIJERAS DE PODA", "$190.00", R.drawable.planta_2, "MACETAS Y ACCESORIOS"),
                PlantItem("FERTILIZANTE", "$130.00", R.drawable.cat_macetas, "MACETAS Y ACCESORIOS")
            )
            "CUIDADOS Y BIENESTAR" -> listOf(
                PlantItem("HUMIDIFICADOR", "$650.00", R.drawable.cat_cuidados, "CUIDADOS Y BIENESTAR"),
                PlantItem("MEDIDOR HUMEDAD", "$210.00", R.drawable.planta_1, "CUIDADOS Y BIENESTAR"),
                PlantItem("GUÍA BOTÁNICA", "$280.00", R.drawable.cat_cuidados, "CUIDADOS Y BIENESTAR"),
                PlantItem("ACEITE DE NEEM", "$160.00", R.drawable.planta_2, "CUIDADOS Y BIENESTAR"),
                PlantItem("JABÓN POTÁSICO", "$140.00", R.drawable.cat_cuidados, "CUIDADOS Y BIENESTAR")
            )
            else -> emptyList()
        }
    }
}
