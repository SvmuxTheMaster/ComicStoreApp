package com.example.comicstoreapp.data.remote



import com.example.comicstoreapp.data.remote.dto.comic.ComicDto
import com.example.comicstoreapp.data.remote.dto.comic.ImageUploadResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface ComicsApi {

    @GET("comics")
    suspend fun obtenerComics(): List<ComicDto>

    @GET("comics/{id}")
    suspend fun obtenerComicPorId(@Path("id") id: Int): ComicDto

    @POST("comics")
    suspend fun crearComic(@Body comic: ComicDto): ComicDto

    @PUT("comics/{id}")
    suspend fun actualizarComic(
        @Path("id") id: Int,
        @Body comic: ComicDto
    ): ComicDto

    @DELETE("comics/{id}")
    suspend fun eliminarComic(@Path("id") id: Int)

    // ⭐ NUEVO: actualizar solo el stock
    @PUT("comics/{id}/actualizar-stock/{stock}")
    suspend fun actualizarStock(
        @Path("id") id: Int,
        @Path("stock") stock: Int
    )

    // Imagen
    @Multipart
    @POST("comics/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): retrofit2.Response<ImageUploadResponse>
}
