package com.example.comicstoreapp.data.local.user

import androidx.room.*
import kotlinx.coroutines.flow.Flow

//@Dao indica que define operaciones para la tabla users.
@Dao
interface UserDao {

    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    suspend fun getAll(): List<UserEntity>

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getByCorreo(correo: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("UPDATE usuarios SET rol = :rol WHERE idUsuario = :id")
    suspend fun updateRole(id: Long, rol: String)

    @Query("DELETE FROM usuarios WHERE idUsuario = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun getAllFlow(): Flow<List<UserEntity>>
}
