package com.example.comicstoreapp.data.repository

import com.example.comicstoreapp.data.local.detalle.DetalleDao
import com.example.comicstoreapp.data.local.detalle.DetalleEntity
import com.example.comicstoreapp.data.local.pedido.PedidoDao
import com.example.comicstoreapp.data.local.pedido.PedidoEntity


class PedidoRepository(
    private val pedidoDao: PedidoDao,
    private val detalleDao: DetalleDao  // ✅ Necesario para insertar detalles
) {

    suspend fun insert(pedido: PedidoEntity): Result<Long> = runCatching {
        pedidoDao.insert(pedido)
    }

    suspend fun crearPedidoConDetalles(
        pedido: PedidoEntity,
        detalles: List<DetalleEntity>
    ): Result<Long> = runCatching {
        // Inserta el pedido y obtiene su ID generado
        val idPedido = pedidoDao.insert(pedido)

        // Inserta cada detalle asignándole el idPedido generado
        detalles.forEach { detalle ->
            detalleDao.insert(detalle.copy(idPedido = idPedido))
        }

        idPedido
    }

    suspend fun update(pedido: PedidoEntity): Result<Unit> = runCatching {
        pedidoDao.update(pedido)
    }

    suspend fun getAll(): Result<List<PedidoEntity>> = runCatching {
        pedidoDao.getAll()
    }

    suspend fun getById(id: Long): Result<PedidoEntity?> = runCatching {
        pedidoDao.getById(id)
    }

    suspend fun delete(pedido: PedidoEntity): Result<Unit> = runCatching {
        pedidoDao.delete(pedido)
    }
}
