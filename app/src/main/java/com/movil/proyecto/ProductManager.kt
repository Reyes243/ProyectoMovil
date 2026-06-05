
package com.movil.proyecto

import android.content.Context
import android.net.Uri
import android.util.Log
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
            try {
                val snapshot = db.collection("productos").limit(1).get().await()
                if (snapshot.isEmpty) {
                    seedInitialProducts()
                }
            } catch (e: Exception) {
                Log.e("ProductManager", "Error init: ${e.message}")
            }
        }
    }

    private suspend fun seedInitialProducts() {
        val initialPlants = listOf(
            hashMapOf("nombre" to "MONSTERA DELICIOSA", "precio" to 250.0, "categoria" to "PLANTAS DE INTERIOR", "estado" to "activo", "stock" to 45L, "ventas" to 0L),
            hashMapOf("nombre" to "LAVANDA", "precio" to 80.0, "categoria" to "PLANTAS DE EXTERIOR", "estado" to "activo", "stock" to 200L, "ventas" to 0L),
            hashMapOf("nombre" to "ALOE VERA", "precio" to 90.0, "categoria" to "BAJO MANTENIMIENTO", "estado" to "activo", "stock" to 90L, "ventas" to 0L)
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
                    category = doc.getString("categoria") ?: category.uppercase(),
                    isUserAdded = doc.getString("vendedor_id") != null,
                    stock = doc.getLong("stock")?.toInt() ?: 0,
                    description = doc.getString("descripcion") ?: "Producto de alta calidad para el bienestar de tu hogar.",
                    salesCount = doc.getLong("ventas")?.toInt() ?: 0
                )
            }
        } catch (e: Exception) {
            Log.e("ProductManager", "Error getProducts: ${e.message}")
            emptyList()
        }
    }

    suspend fun saveProduct(
        context: Context, 
        id: String?, 
        name: String, 
        price: String, 
        category: String, 
        imageUri: Uri? = null, 
        stock: Int = 0, 
        description: String = "",
        existingImageUrl: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val priceValue = price.replace("$", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
        
        var finalImageUrl: String? = existingImageUrl
        
        // Si el usuario seleccionó una NUEVA imagen, la subimos
        if (imageUri != null) {
            try {
                val fileName = "productos/${UUID.randomUUID()}.jpg"
                val ref = storage.reference.child(fileName)
                
                val inputStream = context.contentResolver.openInputStream(imageUri)
                if (inputStream != null) {
                    val bytes = inputStream.readBytes()
                    inputStream.close()
                    ref.putBytes(bytes).await()
                    finalImageUrl = ref.downloadUrl.await().toString()
                    Log.d("ProductManager", "Nueva imagen subida: $finalImageUrl")
                }
            } catch (e: Exception) {
                Log.e("ProductManager", "Error al subir imagen: ${e.message}")
            }
        }

        val productMap = hashMapOf(
            "nombre" to name.uppercase(),
            "precio" to priceValue,
            "categoria" to category.uppercase(),
            "vendedor_id" to UserManager.currentUser?.id,
            "imageUrl" to finalImageUrl,
            "estado" to "activo",
            "stock" to stock.toLong(),
            "descripcion" to description
        )

        return@withContext try {
            if (id.isNullOrEmpty()) {
                // Modo crear nuevo
                productMap["ventas"] = 0L
                db.collection("productos").add(productMap).await()
                Log.d("ProductManager", "Producto creado exitosamente")
            } else {
                // Modo editar: actualizamos el documento existente por su ID
                db.collection("productos").document(id).update(productMap as Map<String, Any>).await()
                Log.d("ProductManager", "Producto $id actualizado exitosamente")
            }
            true
        } catch (e: Exception) {
            Log.e("ProductManager", "Error al guardar producto: ${e.message}")
            false
        }
    }

    suspend fun updateProductSales(productId: String, quantity: Int) = withContext(Dispatchers.IO) {
        if (productId.isEmpty()) return@withContext
        try {
            val docRef = db.collection("productos").document(productId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentStock = (snapshot.getLong("stock") ?: 0L)
                val currentSales = (snapshot.getLong("ventas") ?: 0L)
                
                transaction.update(docRef, "stock", currentStock - quantity)
                transaction.update(docRef, "ventas", currentSales + quantity)
            }.await()
        } catch (e: Exception) {
            Log.e("ProductManager", "Error updating sales: ${e.message}")
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
            Log.e("ProductManager", "Error getUserProducts: ${e.message}")
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
            Log.e("ProductManager", "Error deleting: ${e.message}")
        }
    }
}
