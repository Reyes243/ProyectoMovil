
package com.movil.proyecto.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM usuario WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun register(user: UserEntity): Long

    @Query("SELECT * FROM usuario WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}


@Dao
interface ProductDao {
    @Query("SELECT * FROM producto WHERE categoria_categoria_id = (SELECT categoria_id FROM categoria WHERE nombre = :categoryName)")
    fun getProductsByCategory(categoryName: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM producto WHERE vendedor_id = :userId OR (usuario_id = :userId AND vendedor_id IS NULL)")
    fun getUserProducts(userId: Int): Flow<List<ProductEntity>>

    @Insert
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT * FROM producto WHERE nombre = :name LIMIT 1")
    suspend fun getProductByName(name: String): ProductEntity?

    @Query("SELECT * FROM producto WHERE producto_id = :id LIMIT 1")
    suspend fun getProductById(id: Int): ProductEntity?
}


@Dao
interface OrderDao {
    @Query("SELECT * FROM compra WHERE usuario_usuario_id = :userId ORDER BY fecha DESC")
    fun getUserOrders(userId: Int): Flow<List<OrderEntity>>

    @Insert
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert
    suspend fun insertOrderDetails(details: List<OrderDetailEntity>)

    @Query("SELECT * FROM detalle_compra WHERE compra_compra_id = :orderId")
    suspend fun getOrderDetails(orderId: Int): List<OrderDetailEntity>
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categoria")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Insert
    suspend fun insertCategories(categories: List<CategoryEntity>)
}

@Dao
interface CartDao {
    @Query("SELECT * FROM carrito WHERE usuario_usuario_id = :userId")
    fun getCartByUserId(userId: Int): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cart: CartEntity)

    @Delete
    suspend fun removeCartItem(cart: CartEntity)

    @Query("DELETE FROM carrito WHERE usuario_usuario_id = :userId")
    suspend fun clearCart(userId: Int)

    @Query("SELECT * FROM carrito WHERE usuario_usuario_id = :userId AND producto_producto_id = :productId LIMIT 1")
    suspend fun getCartItem(userId: Int, productId: Int): CartEntity?
}

