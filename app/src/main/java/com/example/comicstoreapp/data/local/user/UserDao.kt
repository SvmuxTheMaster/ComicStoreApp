package com.example.comicstoreapp.data.local.user

import androidx.room.*

//@Dao indica que define operaciones para la tabla users.
@Dao
interface UserDao {

    // Inserta un usuario. ABORT si hay conflicto de PK (no de email; ese lo controlamos a mano).
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    // Devuelve un usuario por email (o null si no existe).
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getByCorreo(correo: String): UserEntity?

    // Cuenta total de usuarios (para saber si hay datos y/o para seeds).
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun count(): Int

    // Lista completa (útil para debug/administración).
    @Query("SELECT * FROM usuarios ORDER BY idUsuario ASC")
    suspend fun getAll(): List<UserEntity>
}