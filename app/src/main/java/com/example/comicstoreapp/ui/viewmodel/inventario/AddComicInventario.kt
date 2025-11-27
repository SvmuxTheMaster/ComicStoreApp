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


data class AddComicUiState(
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

class AdminAddComicViewModel(
    private val repo: ComicRepository = ComicRepository()
) : ViewModel() {

    var uiState = androidx.compose.runtime.mutableStateOf(AddComicUiState())
        private set


    // ACTUALIZACIÓN DE CAMPOS
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


    // SUBIR IMAGEN
    fun uploadImage(context: Context, uri: Uri) {
        uiState.value = uiState.value.copy(uploadingImage = true, error = null, success = null)

        viewModelScope.launch {
            val result = repo.uploadImage(context, uri)

            result.fold(
                onSuccess = { url ->
                    uiState.value = uiState.value.copy(
                        imagenUrl = url,
                        uploadingImage = false,
                        success = "Imagen subida correctamente"
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


    // GUARDAR CÓMIC NUEVO (VALIDADO)
    fun guardarComic(onSuccessNavigate: () -> Unit) {
        val s = uiState.value

        // VALIDACIÓN: campos vacíos
        if (
            s.titulo.isBlank() ||
            s.descripcion.isBlank() ||
            s.autor.isBlank() ||
            s.precio.isBlank() ||
            s.cantidad.isBlank() ||
            s.categoria.isBlank()
        ) {
            uiState.value = s.copy(error = "Todos los campos son obligatorios")
            return
        }

        // VALIDACIÓN: precio
        val errPrecio = formatearPesos(s.precio)
        if (errPrecio != null) {
            uiState.value = s.copy(error = errPrecio)
            return
        }

        // VALIDACIÓN: cantidad
        val errCantidad = validarCantidad(s.cantidad)
        if (errCantidad != null) {
            uiState.value = s.copy(error = errCantidad)
            return
        }

        uiState.value = s.copy(loading = true, error = null, success = null)

        val comic = ComicDto(
            id = 0,
            titulo = s.titulo,
            descripcion = s.descripcion,
            autor = s.autor,
            precio = s.precio.toInt(),
            cantidad = s.cantidad.toInt(),
            imagenUrl = s.imagenUrl,
            categoria = s.categoria
        )

        viewModelScope.launch {
            val result = repo.crearComic(comic)

            result.fold(
                onSuccess = {
                    uiState.value = uiState.value.copy(
                        loading = false,
                        success = "Cómic creado correctamente"
                    )
                    onSuccessNavigate()
                },
                onFailure = { e ->
                    uiState.value = uiState.value.copy(
                        loading = false,
                        error = e.message ?: "Error al crear el cómic"
                    )
                }
            )
        }
    }
}