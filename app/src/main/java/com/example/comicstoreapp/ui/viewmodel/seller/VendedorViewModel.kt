package com.example.comicstoreapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import com.example.comicstoreapp.data.repository.InventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VendedorUiState(
    val inventario: List<InventarioEntity> = emptyList(),
    val loading: Boolean = false,
    val errorMsg: String? = null,
    val successMsg: String? = null
)

class VendedorViewModel(private val repository: InventarioRepository) : ViewModel() {

    private val _state = MutableStateFlow(VendedorUiState())
    val state: StateFlow<VendedorUiState> = _state

    init {
        cargarInventario()
    }

    fun cargarInventario() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMsg = null) }
            val res = repository.getAll()
            if (res.isSuccess) {
                _state.update { it.copy(inventario = res.getOrNull() ?: emptyList(), loading = false) }
            } else {
                _state.update { it.copy(loading = false, errorMsg = res.exceptionOrNull()?.message) }
            }
        }
    }

    fun buscarProducto(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMsg = null) }
            try {
                val all = repository.getAll().getOrNull() ?: emptyList()

                val resultados = all.filter { producto ->
                    val queryLower = query.lowercase()
                    val esIdCoincide = query.toIntOrNull()?.let { producto.idProducto.toInt() == it } ?: false
                    val esTextoCoincide = producto.titulo.lowercase().contains(queryLower) ||
                            producto.categoria.lowercase().contains(queryLower)

                    esIdCoincide || esTextoCoincide
                }

                _state.update { it.copy(inventario = resultados, loading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, errorMsg = e.message) }
            }
        }
    }

    fun actualizarStock(item: InventarioEntity, nuevoStock: Int) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMsg = null, successMsg = null) }
            val actualizado = item.copy(stock = nuevoStock)
            val res = repository.update(actualizado)
            if (res.isSuccess) {
                _state.update { it.copy(successMsg = "Stock actualizado correctamente") }
                cargarInventario()
            } else {
                _state.update { it.copy(errorMsg = "Error al actualizar el stock") }
            }
        }
    }

    fun eliminarProducto(item: InventarioEntity) {
        viewModelScope.launch {
            val res = repository.delete(item)
            if (res.isSuccess) {
                _state.update { it.copy(successMsg = "Producto eliminado correctamente") }
                cargarInventario()
            } else {
                _state.update { it.copy(errorMsg = "Error al eliminar producto") }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(successMsg = null, errorMsg = null) }
    }
}