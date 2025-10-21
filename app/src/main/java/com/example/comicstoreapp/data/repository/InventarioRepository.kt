package com.example.comicstoreapp.data.repository

import com.example.comicstoreapp.data.local.inventario.InventarioDao
import com.example.comicstoreapp.data.local.inventario.InventarioEntity


class InventarioRepository ( private val inventarioDao: InventarioDao) {

    suspend fun getAll(): Result<List<InventarioEntity>> = try {
        Result.success(inventarioDao.getAll())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun insert(producto: InventarioEntity): Result<Unit> = try {
        inventarioDao.insert(producto)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun delete(producto: InventarioEntity): Result<Unit> = try {
        inventarioDao.delete(producto)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun update(producto: InventarioEntity): Result<Unit> = try {
        inventarioDao.update(producto)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

}