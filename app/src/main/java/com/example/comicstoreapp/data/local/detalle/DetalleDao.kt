package com.example.comicstoreapp.data.local.detalle

import androidx.room.*

@Dao
interface DetalleDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(detalle: DetalleEntity): Long

    @Query("SELECT * FROM detalle WHERE idPedido = :pedidoId")
    suspend fun getByPedido(pedidoId: Long): List<DetalleEntity>

    @Update
    suspend fun update(detalle: DetalleEntity)

    @Delete
    suspend fun delete(detalle: DetalleEntity)

    @Query("SELECT * FROM detalle ORDER BY idDetalle ASC")
    suspend fun getAll(): List<DetalleEntity>
}