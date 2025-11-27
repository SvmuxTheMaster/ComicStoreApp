package com.example.comicstoreapp.data.remote.dto.pedido


data class PedidoRequest(
    val usuarioId: Int,
    val items: List<PedidoItemRequest>
)
