package com.example.comicstoreapp.data.remote.dto.pedido


data class PedidoResponse(
    val id: Int,
    val usuarioId: Int,
    val fecha: String,
    val total: Int,
    val estado: String,
    val items: List<PedidoItemResponse>
)

