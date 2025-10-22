package com.example.comicstoreapp.data.local.inventario

import androidx.room.*


@Dao
interface InventarioDao {

    // Insertar un nuevo producto
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(producto: InventarioEntity): Long

    // Actualizar un producto existente
    @Update
    suspend fun update(producto: InventarioEntity)

    // Eliminar un producto
    @Delete
    suspend fun delete(producto: InventarioEntity)

    // Obtener un producto por ID
    @Query("SELECT * FROM inventario WHERE idProducto = :id LIMIT 1")
    suspend fun getById(id: Long): InventarioEntity?

    // Obtener todos los productos
    @Query("SELECT * FROM inventario ORDER BY titulo ASC")
    suspend fun getAll(): List<InventarioEntity>

    // Buscar productos por título o categoría
    @Query("SELECT * FROM inventario WHERE titulo LIKE :query OR categoria LIKE :query ORDER BY titulo ASC")
    suspend fun search(query: String): List<InventarioEntity>

    // 🟢 Actualizar solo el stock (para uso del vendedor)
    @Query("UPDATE inventario SET stock = :nuevoStock WHERE idProducto = :idProducto")
    suspend fun updateStock(idProducto: Long, nuevoStock: Int)

}