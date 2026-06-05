
package com.movil.proyecto.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "usuario")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val usuario_id: Int = 0,
    val nombre: String?,
    val apellidos: String?,
    val email: String?,
    val password: String?,
    val rol: String = "cliente",
    val direccion: String?,
    val telefono: String?,
    val fecha_registro: String = "" // Simplificado para Room
)

@Entity(tableName = "categoria")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoria_id: Int = 0,
    val nombre: String?,
    val descripcion: String?
)

@Entity(
    tableName = "producto",
    foreignKeys = [
        ForeignKey(entity = CategoryEntity::class, parentColumns = ["categoria_id"], childColumns = ["categoria_categoria_id"]),
        ForeignKey(entity = UserEntity::class, parentColumns = ["usuario_id"], childColumns = ["usuario_id"]),
        ForeignKey(entity = UserEntity::class, parentColumns = ["usuario_id"], childColumns = ["vendedor_id"])
    ],
    indices = [Index("categoria_categoria_id"), Index("usuario_id"), Index("vendedor_id")]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val producto_id: Int = 0,
    val nombre: String?,
    val descripcion: String?,
    val precio: Double?,
    val descuento: Double = 0.0,
    val stock: Int?,
    val usuario_id: Int,
    val imagen: String?,
    val imagen_extra1: String?,
    val imagen_extra2: String?,
    val estado: String?, // 'activo' o 'inactivo'
    val categoria_categoria_id: Int?,
    val vendedor_id: Int?
)

@Entity(
    tableName = "carrito",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["usuario_id"], childColumns = ["usuario_usuario_id"]),
        ForeignKey(entity = ProductEntity::class, parentColumns = ["producto_id"], childColumns = ["producto_producto_id"])
    ],
    indices = [Index("usuario_usuario_id"), Index("producto_producto_id")]
)
data class CartEntity(
    @PrimaryKey(autoGenerate = true) val carrito_id: Int = 0,
    val usuario_usuario_id: Int?,
    val producto_producto_id: Int?,
    val cantidad: Int?,
    val fecha_agregado: String = ""
)

@Entity(
    tableName = "compra",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["usuario_id"], childColumns = ["usuario_usuario_id"])
    ],
    indices = [Index("usuario_usuario_id")]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val compra_id: Int = 0,
    val fecha: String = "",
    val total: Double?,
    val nombre_envio: String?,
    val direccion_envio: String?,
    val ciudad_envio: String?,
    val telefono_envio: String?,
    val usuario_usuario_id: Int?
)

@Entity(
    tableName = "detalle_compra",
    foreignKeys = [
        ForeignKey(entity = OrderEntity::class, parentColumns = ["compra_id"], childColumns = ["compra_compra_id"]),
        ForeignKey(entity = ProductEntity::class, parentColumns = ["producto_id"], childColumns = ["producto_producto_id"])
    ],
    indices = [Index("compra_compra_id"), Index("producto_producto_id")]
)
data class OrderDetailEntity(
    @PrimaryKey(autoGenerate = true) val detalle_id: Int = 0,
    val cantidad: Int?,
    val precio_unitario: Double?,
    val subtotal: Double?,
    val comision_plataforma: Double = 0.0,
    val compra_compra_id: Int?,
    val producto_producto_id: Int?
)

@Entity(
    tableName = "pago",
    foreignKeys = [
        ForeignKey(entity = OrderEntity::class, parentColumns = ["compra_id"], childColumns = ["compra_compra_id"])
    ],
    indices = [Index("compra_compra_id")]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val pago_id: Int = 0,
    val compra_compra_id: Int,
    val correo: String?,
    val titular_tarjeta: String?,
    val ultimos_digitos: String?,
    val dir_facturacion: String?,
    val ciudad_fact: String?,
    val cp_fact: String?,
    val pais_fact: String?,
    val fecha_pago: String = ""
)
