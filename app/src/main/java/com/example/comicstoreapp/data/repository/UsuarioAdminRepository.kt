package com.example.comicstoreapp.data.repository


import com.example.comicstoreapp.data.remote.AdminApi
import com.example.comicstoreapp.data.remote.RemoteModule
import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioAdminRepository {

    private val api = RemoteModule.create(AdminApi::class.java)


    suspend fun listarUsuarios(rol: String): Result<List<UsuarioDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val res = api.listarUsuarios(rol)
                if (res.isSuccessful) {
                    Result.success(res.body() ?: emptyList())
                } else {
                    Result.failure(Exception("Error: ${res.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    suspend fun actualizarRol(rol: String, id: Long, nuevoRol: String): Result<UsuarioDto> {
        return try {
            val res = api.actualizarRol(rol, id, nuevoRol)
            if (res.isSuccessful) {
                Result.success(res.body()!!)
            } else {
                Result.failure(Exception(res.errorBody()?.string() ?: "Error actualizando rol"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun eliminarUsuario(rol: String, id: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val res = api.eliminarUsuario(rol, id)
                if (res.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al eliminar: ${res.code()}"))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
