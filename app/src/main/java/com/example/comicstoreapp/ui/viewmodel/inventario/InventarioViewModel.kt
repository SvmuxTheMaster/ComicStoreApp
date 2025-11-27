package com.example.comicstoreapp.ui.viewmodel.inventario


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.remote.dto.comic.ComicDto
import com.example.comicstoreapp.data.repository.ComicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InventarioUiState(
    val comics: List<ComicDto> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class InventarioViewModel(
    private val repository: ComicRepository = ComicRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventarioUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarInventario() {
        _uiState.value = _uiState.value.copy(loading = true, error = null)

        viewModelScope.launch {
            val result = repository.getAllComics()
            _uiState.value = result.fold(
                onSuccess = { lista ->
                    InventarioUiState(
                        comics = lista,
                        loading = false,
                        error = null
                    )
                },
                onFailure = { e ->
                    InventarioUiState(
                        comics = emptyList(),
                        loading = false,
                        error = e.message ?: "Error al cargar inventario"
                    )
                }
            )
        }
    }
}
