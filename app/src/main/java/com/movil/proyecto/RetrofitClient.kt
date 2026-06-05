
package com.movil.proyecto

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Aquí pondrás el link que te dé Firebase al desplegar tus funciones
    private const val BASE_URL = "https://us-central1-raiz-viva-6ba6d.cloudfunctions.net/"

    val instance: ProductApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ProductApiService::class.java)
    }
}
