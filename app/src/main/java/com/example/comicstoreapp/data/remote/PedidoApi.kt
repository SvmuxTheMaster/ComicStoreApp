package com.example.comicstoreapp.data.remote

import com.example.comicstoreapp.data.remote.dto.pedido.PedidoRequest
import com.example.comicstoreapp.data.remote.dto.pedido.PedidoResponse
import retrofit2.Response
import retrofit2.http.*


interface PedidoApi {

    // Crear pedido
    @POST("pedidos")
    suspend fun crearPedido(
        @Body body: PedidoRequest
    ): Response<PedidoResponse>

    // Pedidos por usuario
    @GET("pedidos/usuario/{id}")
    suspend fun obtenerPedidosPorUsuario(
        @Path("id") userId: Int
    ): List<PedidoResponse>

    // Obtener un pedido
    @GET("pedidos/{id}")
    suspend fun obtenerPedido(
        @Path("id") id: Int
    ): PedidoResponse

    // Obtener TODOS los pedidos (para vendedor)
    @GET("pedidos")
    suspend fun obtenerPedidos(): List<PedidoResponse>

    // Actualizar estado (PENDIENTE, PREPARANDO, ENVIADO, ENTREGADO)
    @PUT("pedidos/{id}/estado/{estado}")
    suspend fun actualizarEstado(
        @Path("id") idPedido: Int?,
        @Path("estado") nuevoEstado: String
    ): Response<PedidoResponse>

    // Cancelar pedido (devuelve stock)
    @PUT("pedidos/{id}/cancelar")
    suspend fun cancelarPedido(
        @Path("id") idPedido: Int
    ): Response<PedidoResponse>

    // Eliminar pedido
    @DELETE("pedidos/{id}")
    suspend fun eliminarPedido(
        @Path("id") idPedido: Int
    ): Response<Unit>
}

