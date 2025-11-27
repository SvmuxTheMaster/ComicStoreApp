package com.example.comicstoreapp.data.remote.dto.pedido


data class PedidoItemResponse(
    val id: Int,
    val comicId: Int,
    val cantidad: Int,
    val precioUnitario: Int
)