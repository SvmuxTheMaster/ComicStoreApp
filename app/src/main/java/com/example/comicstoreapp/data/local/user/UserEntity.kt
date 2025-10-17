package com.example.comicstoreapp.data.local.user

import androidx.room.*


@Entity(tableName = "usuarios")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val idUsuario: Long = 0L,

    val nombre: String,
    val rut: String,
    val correo: String,
    val contrasena: String,
    val rol: String = "usuario" // "usuario", "vendedor" o "admin"
)
