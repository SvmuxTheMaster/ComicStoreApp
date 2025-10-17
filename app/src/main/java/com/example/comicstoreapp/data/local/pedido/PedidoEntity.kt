package com.example.comicstoreapp.data.local.pedido

import androidx.room.*


@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val idPedido: Long = 0L,

    val idusuario: Long,
    val fecha: String,
    val estado: String  // "pendiente", "en proceso" o "entregado"
)

