
package com.movil.proyecto

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.UUID

object ProductManager {
    private val _catalog = mutableStateListOf<PlantItem>()
    val catalog: List<PlantItem> get() = _catalog

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    fun init(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val snapshot = db.collection("productos").limit(1).get().await()
            if (snapshot.isEmpty) {
                seedInitialProducts()
            }
        }
    }

    private suspend fun seedInitialProducts() {
        val initialPlants = listOf(
            hashMapOf("nombre" to "MONSTERA DELICIOSA", "precio" to 250.0, "categoria" to "PLANTAS DE INTERIOR", "estado" to "activo", "stock" to 45, "ventas" to 0),
            hashMapOf("nombre" to "LAVANDA", "precio" to 80.0, "categoria" to "PLANTAS DE EXTERIOR", "estado" to "activo", "stock" to 200, "ventas" to 0),
            hashMapOf("nombre" to "ALOE VERA", "precio" to 90.0, "categoria" to "BAJO MANTENIMIENTO", "estado" to "activo", "stock" to 90, "ventas" to 0)
        )
        for (plant in initialPlants) {
            db.collection("productos").add(plant).await()
        }
    }

    private fun formatPrice(price: Double): String {
        return if (price == price.toLong().toDouble()) {
            "$ ${price.toLong()}"
        } else {
            "$ ${String.format(java.util.Locale.US, "%.2f", price)}"
        }
    }

    suspend fun getProductsByCategory(category: String): List<PlantItem> = withContext(Dispatchers.IO) {
        try {
            val result = db.collection("productos")
                .whereEqualTo("categoria", category.uppercase())
                .whereEqualTo("estado", "activo")
                .get()
                .await()
            
            result.documents.map { doc ->
                val priceNum = doc.getDouble("precio") ?: 0.0
                PlantItem(
                    id = doc.id,
                    name = doc.getString("nombre")?.uppercase() ?: "SIN NOMBRE",
                    price = formatPrice(priceNum),
                    imageUrl = doc.getString("imageUrl"),
                    imageRes = 0,
                    category = category.uppercase(),
                    isUserAdded = doc.getString("vendedor_id") != null,
                    stock = doc.getLong("stock")?.toInt() ?: 0,
                    description = doc.getString("descripcion") ?: "Producto de alta calidad para el bienestar de tu hogar.",
                    salesCount = doc.getLong("ventas")?.toInt() ?: 0
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addProduct(name: String, price: String, category: String, imageUri: Uri? = null, stock: Int = 0, description: String = "") = withContext(Dispatchers.IO) {
        val priceValue = price.replace("$", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
        
        var uploadedImageUrl: String? = null
        if (imageUri != null) {
            try {
                val fileName = "productos/${UUID.randomUUID()}.jpg"
                val ref = storage.reference.child(fileName)
                ref.putFile(imageUri).await()
                uploadedImageUrl = ref.downloadUrl.await().toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val productMap = hashMapOf(
            "nombre" to name.uppercase(),
            "precio" to priceValue,
            "categoria" to category.uppercase(),
            "vendedor_id" to UserManager.currentUser?.id,
            "imageUrl" to uploadedImageUrl,
            "estado" to "activo",
            "stock" to stock.toLong(),
            "descripcion" to description,
            "ventas" to 0L
        )
        try {
            db.collection("productos").add(productMap).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateProductSales(productId: String, quantity: Int) = withContext(Dispatchers.IO) {
        if (productId.isEmpty()) return@withContext
        try {
            db.collection("productos").document(productId).update(
                "ventas", FieldValue.increment(quantity.toLong()),
                "stock", FieldValue.increment(-quantity.toLong())
            ).await()
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
                val priceNum = doc.getDouble("precio") ?: 0.0
                PlantItem(
                    id = doc.id,
                    name = doc.getString("nombre")?.uppercase() ?: "SIN NOMBRE",
                    price = formatPrice(priceNum),
                    imageUrl = doc.getString("imageUrl"),
                    imageRes = 0,
                    category = doc.getString("categoria") ?: "",
                    isUserAdded = true,
                    stock = doc.getLong("stock")?.toInt() ?: 0,
                    description = doc.getString("descripcion") ?: "Producto de alta calidad para el bienestar de tu hogar.",
                    salesCount = doc.getLong("ventas")?.toInt() ?: 0
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
                PlantItem("MONSTERA DELICIOSA", "$ 250", R.drawable.monstera_1, null, "PLANTAS DE INTERIOR", stock = 45),
                PlantItem("POTO (EPIPREMNUM)", "$ 120", R.drawable.planta_1, null, "PLANTAS DE INTERIOR", stock = 120)
            )
            "PLANTAS DE EXTERIOR" -> listOf(
                PlantItem("LAVANDA", "$ 80", R.drawable.cat_exterior, null, "PLANTAS DE EXTERIOR", stock = 200)
            )
            else -> emptyList()
        }
    }
}
