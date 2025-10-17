package com.example.comicstoreapp.data.local.detalle

import androidx.room.*
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import com.example.comicstoreapp.data.local.pedido.PedidoEntity

@Entity(
    tableName = "detalle",
    foreignKeys = [
        ForeignKey(
            entity = PedidoEntity::class,
            parentColumns = ["idPedido"],
            childColumns = ["idPedido"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = InventarioEntity::class,
            parentColumns = ["idProducto"],
            childColumns = ["idProducto"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("idPedido"),
        Index("idProducto")
    ]
)
data class DetalleEntity(
    @PrimaryKey(autoGenerate = true)
    val idDetalle: Long = 0L,

    val idPedido: Long,       // FK a Pedido
    val idProducto: Long,     // FK a Inventario
    val cantidad: Int,
    val precioUnidad: Int
)
