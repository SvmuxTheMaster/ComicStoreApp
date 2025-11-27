package com.example.comicstoreapp.ui.viewmodel.inventario

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.remote.dto.comic.ComicDto
import com.example.comicstoreapp.data.repository.ComicRepository
import com.example.comicstoreapp.domain.validation.formatearPesos
import com.example.comicstoreapp.domain.validation.validarCantidad
import kotlinx.coroutines.launch

data class EditComicUiState(
    val id: Int? = null,
    val titulo: String = "",
    val descripcion: String = "",
    val autor: String = "",
    val precio: String = "",
    val cantidad: String = "",
    val imagenUrl: String = "",
    val categoria: String = "",
    val loading: Boolean = false,
    val uploadingImage: Boolean = false,
    val error: String? = null,
    val success: String? = null
)


class AdminEditComicViewModel(
    private val repo: ComicRepository = ComicRepository()
) : ViewModel() {

    var uiState = androidx.compose.runtime.mutableStateOf(EditComicUiState())
        private set


    // CARGAR DATOS DEL CÓMIC EXISTENTE
    fun cargarComic(id: Int) {
        uiState.value = uiState.value.copy(
            loading = true,
            error = null,
            success = null
        )

        viewModelScope.launch {
            val result = repo.obtenerComic(id)

            uiState.value = result.fold(
                onSuccess = { comic ->
                    EditComicUiState(
                        id = comic.id,
                        titulo = comic.titulo,
                        descripcion = comic.descripcion,
                        autor = comic.autor,
                        precio = comic.precio.toString(),
                        cantidad = comic.cantidad.toString(),
                        imagenUrl = comic.imagenUrl ?: "",
                        categoria = comic.categoria,
                        loading = false
                    )
                },
                onFailure = { e ->
                    uiState.value.copy(
                        loading = false,
                        error = e.message ?: "No se pudo cargar el cómic"
                    )
                }
            )
        }
    }


    // ACTUALIZAR CAMPOS
    fun onTituloChange(v: String) {
        uiState.value = uiState.value.copy(titulo = v)
    }

    fun onDescripcionChange(v: String) {
        uiState.value = uiState.value.copy(descripcion = v)
    }

    fun onAutorChange(v: String) {
        uiState.value = uiState.value.copy(autor = v)
    }

    fun onPrecioChange(v: String) {
        if (v.isEmpty() || v.all { it.isDigit() }) {
            uiState.value = uiState.value.copy(precio = v)
        }
    }

    fun onCantidadChange(v: String) {
        if (v.isEmpty() || v.all { it.isDigit() }) {
            uiState.value = uiState.value.copy(cantidad = v)
        }
    }

    fun onCategoriaChange(v: String) {
        uiState.value = uiState.value.copy(categoria = v)
    }


    // SUBIR IMAGEN (OPCIONAL)
    fun uploadImage(context: Context, uri: Uri) {
        uiState.value = uiState.value.copy(uploadingImage = true, error = null, success = null)

        viewModelScope.launch {
            val result = repo.uploadImage(context, uri)

            result.fold(
                onSuccess = { url ->
                    uiState.value = uiState.value.copy(
                        imagenUrl = url,
                        uploadingImage = false,
                        success = "Imagen actualizada correctamente"
                    )
                },
                onFailure = { e ->
                    uiState.value = uiState.value.copy(
                        uploadingImage = false,
                        error = e.message ?: "Error al subir imagen"
                    )
                }
            )
        }
    }


    // VALIDAR Y ACTUALIZAR
    fun actualizarComic(onSuccessNavigate: () -> Unit) {
        val s = uiState.value

        // VALIDACIONES
        val errPrecio = formatearPesos(s.precio)
        val errCantidad = validarCantidad(s.cantidad)

        if (errPrecio != null || errCantidad != null) {
            uiState.value = s.copy(error = errPrecio ?: errCantidad)
            return
        }

        if (
            s.titulo.isBlank() ||
            s.descripcion.isBlank() ||
            s.autor.isBlank() ||
            s.categoria.isBlank() ||
            s.id == null
        ) {
            uiState.value = s.copy(error = "Todos los campos son obligatorios")
            return
        }

        val comic = ComicDto(
            id = s.id!!,
            titulo = s.titulo,
            descripcion = s.descripcion,
            autor = s.autor,
            precio = s.precio.toInt(),
            cantidad = s.cantidad.toInt(),
            imagenUrl = s.imagenUrl,
            categoria = s.categoria
        )

        uiState.value = s.copy(loading = true, error = null, success = null)

        viewModelScope.launch {
            val result = repo.actualizarComic(s.id, comic)

            result.fold(
                onSuccess = {
                    uiState.value = uiState.value.copy(
                        loading = false,
                        success = "Cómic actualizado correctamente"
                    )
                    onSuccessNavigate()
                },
                onFailure = { e ->
                    uiState.value = uiState.value.copy(
                        loading = false,
                        error = e.message ?: "Error al guardar los cambios"
                    )
                }
            )
        }
    }
}
