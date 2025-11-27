package com.example.comicstoreapp.data.repository


import com.example.comicstoreapp.data.remote.ComicStoreApi
import com.example.comicstoreapp.data.remote.RemoteModule
import com.example.comicstoreapp.data.remote.dto.login.LoginRequest
import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioDto
import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioRegisterDto
import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioUpdateDto


class UsuarioRepository(
    private val api: ComicStoreApi = RemoteModule.create(ComicStoreApi::class.java)
) {

    suspend fun login(email: String, password: String): Result<UsuarioDto> = try {
        val usuario = api.login(LoginRequest(email, password))
        Result.success(usuario)
    } catch (e: Exception) {
        Result.failure(e)
    }


    suspend fun register(nombre: String, rut: String, email: String, password: String): Result<UsuarioDto> {
        return try {
            val user = UsuarioRegisterDto(
                nombre = nombre,
                rut = rut,
                email = email,
                password = password
            )

            val response = api.register(user)
            Result.success(response)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarEmailPassword(id: Long, email: String, password: String): Result<UsuarioDto> {
        return try {
            val req = UsuarioUpdateDto(email = email, password = password)
            val res = api.actualizarEmailPassword(id, req)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




}
