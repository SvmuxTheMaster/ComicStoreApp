package com.example.comicstoreapp.data.local.inventario

import androidx.room.*

@Entity(tableName = "inventario")
data class InventarioEntity(
    @PrimaryKey(autoGenerate = true)
    val idProducto: Long = 0L,

    val titulo: String,
    val autor: String,
    val descripcion: String,
    val categoria: String,
    val precio: Int,
    val stock: Int
)
