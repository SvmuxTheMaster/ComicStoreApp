package com.example.comicstoreapp.data.local.pedido

import androidx.room.*

@Dao
interface PedidoDao {

    // Inserta un pedido nuevo
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pedido: PedidoEntity): Long

    // Actualiza un pedido existente (por ejemplo, cambiar estado)
    @Update
    suspend fun update(pedido: PedidoEntity)

    // Obtiene un pedido por su id
    @Query("SELECT * FROM pedidos WHERE idPedido = :id LIMIT 1")
    suspend fun getById(id: Long): PedidoEntity?

    // Lista todos los pedidos
    @Query("SELECT * FROM pedidos ORDER BY fecha DESC")
    suspend fun getAll(): List<PedidoEntity>

    // Lista pedidos de un usuario específico
    @Query("SELECT * FROM pedidos WHERE idusuario = :usuarioId ORDER BY fecha DESC")
    suspend fun getByUsuario(usuarioId: Long): List<PedidoEntity>
}