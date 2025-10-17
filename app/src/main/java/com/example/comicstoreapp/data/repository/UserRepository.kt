package com.example.comicstoreapp.data.repository

import com.example.comicstoreapp.data.local.user.UserDao
import com.example.comicstoreapp.data.local.user.UserEntity


// Repositorio: orquesta reglas de negocio para login/registro sobre el DAO.
class UserRepository(
    private val userDao: UserDao // Inyección del DAO
) {

    // Login: busca por email y valida contraseña
    suspend fun login(correo: String, contrasena: String): Result<UserEntity> {
        val user = userDao.getByCorreo(correo)                        // Busca usuario
        return if (user != null && user.contrasena == contrasena) {      // Verifica pass
            Result.success(user)                                     // Éxito
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas")) // Error
        }
    }

    // Registro: valida no duplicado y crea nuevo usuario (con teléfono)
    suspend fun register(nombre: String, correo: String, rut: String, contrasena: String, rol: String): Result<Long> {
        val exists = userDao.getByCorreo(correo) != null               // ¿Correo ya usado?
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insert(                                     // Inserta nuevo
            UserEntity(
                nombre = nombre,
                rut = rut,
                correo = correo,
                contrasena = contrasena,
                rol = rol
            )
        )
        return Result.success(id)                                    // Devuelve ID generado
    }
}