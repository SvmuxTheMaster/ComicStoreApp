package com.example.comicstoreapp.data.repository

import com.example.comicstoreapp.data.remote.PedidoApi
import com.example.comicstoreapp.data.remote.RemotePedidos
import com.example.comicstoreapp.data.remote.dto.pedido.PedidoRequest
import com.example.comicstoreapp.data.remote.dto.pedido.PedidoResponse


class PedidoRepository(
    private val api: PedidoApi = RemotePedidos.create(PedidoApi::class.java)
) {

    suspend fun crearPedido(body: PedidoRequest): Result<Unit> {
        return try {
            val response = api.crearPedido(body)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg =
                    response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                        ?: "Error al crear pedido"

                Result.failure(Exception(errorMsg))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun obtenerPedidosUsuario(usuarioId: Int): Result<List<PedidoResponse>> = try {
        val res = api.obtenerPedidosPorUsuario(usuarioId)
        Result.success(res)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun obtenerTodosLosPedidos(): Result<List<PedidoResponse>> = try {
        val res = api.obtenerPedidos()
        Result.success(res)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun actualizarEstadoPedido(id: Int?, estado: String): Result<Unit> = try {
        api.actualizarEstado(id, estado)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

}
