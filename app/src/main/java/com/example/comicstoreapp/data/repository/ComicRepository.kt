package com.example.comicstoreapp.data.repository


import android.content.Context
import android.net.Uri
import com.example.comicstoreapp.data.remote.ComicsApi
import com.example.comicstoreapp.data.remote.RemoteComics
import com.example.comicstoreapp.data.remote.dto.comic.ComicDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ComicRepository(
    private val api: ComicsApi = RemoteComics.create(ComicsApi::class.java)
) {

    // --------------------------
    // Obtener todos los cómics
    // --------------------------
    suspend fun getAllComics(): Result<List<ComicDto>> = try {
        val res = api.obtenerComics()
        Result.success(res)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --------------------------
    // Actualizar solo el STOCK
    // --------------------------
    suspend fun actualizarStock(id: Int, nuevoStock: Int): Result<Unit> = try {
        api.actualizarStock(id, nuevoStock)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --------------------------
    // Obtener por ID
    // --------------------------
    suspend fun obtenerComic(id: Int): Result<ComicDto> = try {
        val res = api.obtenerComicPorId(id)
        Result.success(res)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --------------------------
    // Crear cómic
    // --------------------------
    suspend fun crearComic(comic: ComicDto): Result<ComicDto> = try {
        val res = api.crearComic(comic)
        Result.success(res)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --------------------------
    // Actualizar comic completo
    // --------------------------
    suspend fun actualizarComic(id: Int, comic: ComicDto): Result<ComicDto> = try {
        val res = api.actualizarComic(id, comic)
        Result.success(res)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --------------------------
    // Eliminar
    // --------------------------
    suspend fun eliminarComic(id: Int): Result<Unit> = try {
        api.eliminarComic(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }



    // --------------------------
    // SUBIR IMAGEN
    // --------------------------
    suspend fun uploadImage(context: Context, uri: Uri): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext Result.failure(Exception("No se pudo abrir la imagen"))

                val tempFile = File.createTempFile("comic_img_", ".jpg", context.cacheDir)

                FileOutputStream(tempFile).use { output ->
                    inputStream.use { it.copyTo(output) }
                }

                val requestFile =
                    tempFile.asRequestBody("image/*".toMediaTypeOrNull())

                val body = MultipartBody.Part.createFormData(
                    name = "file",
                    filename = tempFile.name,
                    body = requestFile
                )

                val response = api.uploadImage(body)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.url)
                } else {
                    Result.failure(Exception("Error al subir imagen"))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}