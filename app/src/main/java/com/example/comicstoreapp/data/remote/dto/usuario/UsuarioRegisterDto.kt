package com.example.comicstoreapp.data.remote.dto.usuario

data class UsuarioRegisterDto(
    val nombre: String,
    val rut: String,
    val email: String,
    val password: String,
    val rol: String = "usuario" // por defecto
)