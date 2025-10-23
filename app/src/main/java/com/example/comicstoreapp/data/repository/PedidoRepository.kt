package com.example.comicstoreapp.data.repository


import com.example.comicstoreapp.data.local.pedido.PedidoDao
import com.example.comicstoreapp.data.local.pedido.PedidoEntity

class PedidoRepository(private val dao: PedidoDao) {

    suspend fun insert(pedido: PedidoEntity): Result<Long> = runCatching {
        dao.insert(pedido)
    }

    suspend fun update(pedido: PedidoEntity): Result<Unit> = runCatching {
        dao.update(pedido)
    }

    suspend fun getAll(): Result<List<PedidoEntity>> = runCatching {
        dao.getAll()
    }

    /* suspend fun getByUsuario(idUsuario: Long): Result<List<PedidoEntity>> = runCatching {
        dao.getByUsuario(idUsuario)
    } */

    suspend fun getById(id: Long): Result<PedidoEntity?> = runCatching {
        dao.getById(id)
    }

    suspend fun delete(pedido: PedidoEntity): Result<Unit> = runCatching {
        dao.delete(pedido)
    }

}
