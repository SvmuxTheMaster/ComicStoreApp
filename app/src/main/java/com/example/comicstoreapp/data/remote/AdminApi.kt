package com.example.comicstoreapp.data.remote

import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioDto
import retrofit2.Response
import retrofit2.http.*


interface AdminApi {

    @GET("usuarios")
    suspend fun listarUsuarios(
        @Header("X-ROL") rol: String
    ): Response<List<UsuarioDto>>

    @PUT("usuarios/{id}/rol")
    suspend fun actualizarRol(
        @Header("X-ROL") rol: String,
        @Path("id") id: Long,
        @Query("nuevoRol") nuevoRol: String
    ): Response<UsuarioDto>


    @DELETE("usuarios/{id}")
    suspend fun eliminarUsuario(
        @Header("X-ROL") rol: String,
        @Path("id") id: Long
    ): Response<Void>
}
