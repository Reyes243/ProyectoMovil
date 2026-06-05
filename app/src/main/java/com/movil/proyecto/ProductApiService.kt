
package com.movil.proyecto

import retrofit2.http.GET

// Esta interfaz le dice a la App cómo pedirle cosas a la API
interface ProductApiService {
    
    // Aquí definimos que queremos obtener la lista de productos
    @GET("getProductsApi")
    suspend fun getProducts(): List<PlantItemResponse>
}

// Clase para recibir la respuesta de la API
data class PlantItemResponse(
    val id: String,
    val nombre: String,
    val precio: Double,
    val categoria: String,
    val imageUrl: String?
)
