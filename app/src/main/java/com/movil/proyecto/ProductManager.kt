
package com.movil.proyecto

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

object ProductManager {
    private val _catalog = mutableStateListOf<PlantItem>()
    val catalog: List<PlantItem> get() = _catalog

    private val db by lazy { FirebaseFirestore.getInstance() }

    fun init(context: Context) {
        // Al iniciar, verificamos si hay que cargar datos iniciales
        CoroutineScope(Dispatchers.IO).launch {
            val snapshot = db.collection("productos").limit(1).get().await()
            if (snapshot.isEmpty) {
                seedInitialProducts()
            }
        }
    }

    private suspend fun seedInitialProducts() {
        val initialPlants = listOf(
            hashMapOf("nombre" to "MONSTERA DELICIOSA", "precio" to 250.0, "categoria" to "PLANTAS DE INTERIOR", "estado" to "activo"),
            hashMapOf("nombre" to "LAVANDA", "precio" to 80.0, "categoria" to "PLANTAS DE EXTERIOR", "estado" to "activo"),
            hashMapOf("nombre" to "ALOE VERA", "precio" to 90.0, "categoria" to "BAJO MANTENIMIENTO", "estado" to "activo")
        )
        for (plant in initialPlants) {
            db.collection("productos").add(plant).await()
        }
    }


    suspend fun getProductsByCategory(category: String): List<PlantItem> = withContext(Dispatchers.IO) {
        try {
            val result = db.collection("productos")
                .whereEqualTo("categoria", category.uppercase())
                .get()
                .await()
            
            val cloudProducts = result.documents.map { doc ->
                PlantItem(
                    name = doc.getString("nombre")?.uppercase() ?: "SIN NOMBRE",
                    price = "$ ${String.format(java.util.Locale.getDefault(), "%.2f", doc.getDouble("precio") ?: 0.0)}",
                    imageRes = R.drawable.logo, 
                    category = category.uppercase(),
                    isUserAdded = doc.getString("vendedor_id") != null
                )
            }
            
            if (cloudProducts.isEmpty()) getHardcodedProducts(category) else cloudProducts
        } catch (e: Exception) {
            e.printStackTrace()
            getHardcodedProducts(category)
        }
    }

    suspend fun addProduct(name: String, price: String, category: String) = withContext(Dispatchers.IO) {
        val priceValue = price.replace("$", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
        val productMap = hashMapOf(
            "nombre" to name.uppercase(),
            "precio" to priceValue,
            "categoria" to category.uppercase(),
            "vendedor_id" to UserManager.currentUser?.id,
            "estado" to "activo"
        )
        try {
            db.collection("productos").add(productMap).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getUserProducts(): List<PlantItem> = withContext(Dispatchers.IO) {
        val userId = UserManager.currentUser?.id ?: return@withContext emptyList<PlantItem>()
        try {
            val result = db.collection("productos")
                .whereEqualTo("vendedor_id", userId)
                .get()
                .await()
            
            result.documents.map { doc ->
                PlantItem(
                    name = doc.getString("nombre")?.uppercase() ?: "SIN NOMBRE",
                    price = "$ ${String.format(java.util.Locale.getDefault(), "%.2f", doc.getDouble("precio") ?: 0.0)}",
                    imageRes = R.drawable.logo, 
                    category = doc.getString("categoria") ?: "",
                    isUserAdded = true
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun deleteProduct(name: String) = withContext(Dispatchers.IO) {

        try {
            val result = db.collection("productos").whereEqualTo("nombre", name.uppercase()).get().await()
            for (doc in result.documents) {
                db.collection("productos").document(doc.id).delete().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getHardcodedProducts(category: String): List<PlantItem> {
        return when (category.uppercase()) {
            "PLANTAS DE INTERIOR" -> listOf(
                PlantItem("MONSTERA DELICIOSA", "$250.00", R.drawable.monstera_1, "PLANTAS DE INTERIOR"),
                PlantItem("POTO (EPIPREMNUM)", "$120.00", R.drawable.planta_1, "PLANTAS DE INTERIOR")
            )
            "PLANTAS DE EXTERIOR" -> listOf(
                PlantItem("LAVANDA", "$80.00", R.drawable.cat_exterior, "PLANTAS DE EXTERIOR")
            )
            else -> emptyList()
        }
    }
}
