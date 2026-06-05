
package com.movil.proyecto.api

import com.movil.proyecto.UserData
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class LoginResponse(
    val status: String,
    val message: String?,
    val user: UserData?
)

data class ProductResponse(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val categoria: String,
    val imagen: String?
)

data class CommonResponse(
    val status: String,
    val message: String?
)

interface ApiService {
    @FormUrlEncoded
    @POST("api/login.php")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("api/register.php")
    suspend fun register(
        @Field("nombre") nombre: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("direccion") direccion: String,
        @Field("telefono") telefono: String
    ): Response<CommonResponse>

    @GET("api/get_products.php")
    suspend fun getProducts(
        @Query("category") category: String? = null
    ): Response<List<ProductResponse>>
}

