package com.example.comicstoreapp.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.remote.dto.comic.ComicDto
import com.example.comicstoreapp.data.repository.ComicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminComicsUiState(
    val comics: List<ComicDto> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminComicsViewModel(
    private val repo: ComicRepository = ComicRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminComicsUiState())
    val uiState: StateFlow<AdminComicsUiState> = _uiState

    init {
        cargarComics()
    }

    fun cargarComics() {
        _uiState.value = _uiState.value.copy(loading = true, error = null, successMessage = null)

        viewModelScope.launch {
            val result = repo.getAllComics()
            _uiState.value = result.fold(
                onSuccess = { lista ->
                    AdminComicsUiState(
                        comics = lista,
                        loading = false
                    )
                },
                onFailure = { e ->
                    AdminComicsUiState(
                        comics = emptyList(),
                        loading = false,
                        error = e.message ?: "Error al cargar productos"
                    )
                }
            )
        }
    }

    fun eliminarComic(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, successMessage = null)

            val result = repo.eliminarComic(id)
            result.fold(
                onSuccess = {
                    cargarComics()
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Producto eliminado correctamente"
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = e.message ?: "No se pudo eliminar el producto"
                    )
                }
            )
        }
    }
}
