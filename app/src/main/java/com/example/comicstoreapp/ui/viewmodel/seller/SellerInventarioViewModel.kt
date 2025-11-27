package com.example.comicstoreapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.remote.dto.comic.ComicDto
import com.example.comicstoreapp.data.repository.ComicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class InventoryUiState(
    val comics: List<ComicDto> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)


class SellerInventarioViewModel(
    private val repo: ComicRepository = ComicRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(InventoryUiState())
    val ui = _ui.asStateFlow()

    fun cargarInventario() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true)

            val res = repo.getAllComics()

            res.fold(
                onSuccess = { _ui.value = InventoryUiState(comics = it) },
                onFailure = { _ui.value = InventoryUiState(error = it.message) }
            )
        }
    }

    fun actualizarStock(id: Int, nuevoStock: Int) {
        viewModelScope.launch {
            repo.actualizarStock(id, nuevoStock)

            cargarInventario() // refresca la lista
        }
    }
}
