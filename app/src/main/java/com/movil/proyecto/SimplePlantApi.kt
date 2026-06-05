
package com.movil.proyecto

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 1. Clase para recibir el consejo (Formato de la API Slip Advice)
data class AdviceResponse(
    val slip: AdviceSlip
)

data class AdviceSlip(
    val id: Int,
    val advice: String
)

// 2. Interfaz de la API
interface SimplePlantApi {
    @GET("advice")
    suspend fun getRandomAdvice(): AdviceResponse
}

// 3. Manager de la API
object PlantApiManager {
    private const val BASE_URL = "https://api.adviceslip.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: SimplePlantApi = retrofit.create(SimplePlantApi::class.java)
}
