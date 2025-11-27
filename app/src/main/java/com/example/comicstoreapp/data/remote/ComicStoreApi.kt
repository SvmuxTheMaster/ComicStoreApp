package com.example.comicstoreapp.data.remote


import com.example.comicstoreapp.data.remote.dto.login.LoginRequest
import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioDto
import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioRegisterDto
import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioUpdateDto
import retrofit2.http.*
interface ComicStoreApi {

    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): UsuarioDto

    @POST("usuarios")
    suspend fun register(@Body user: UsuarioRegisterDto): UsuarioDto

    @PUT("usuarios/{id}")
    suspend fun actualizarEmailPassword(
        @Path("id") id: Long,
        @Body datos: UsuarioUpdateDto
    ): UsuarioDto

}
