package com.example.comicstoreapp.ui.viewmodel.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import com.example.comicstoreapp.data.repository.InventarioRepository
import com.example.comicstoreapp.ui.viewmodel.carro.ItemCarrito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class InventarioUiState(
    val inventario: List<InventarioEntity> = emptyList(),
    val loading: Boolean = false,
    val errorMsg: String? = null,
    val successMsg: String? = null
)

class InventarioViewModel(private val repository: InventarioRepository) : ViewModel(){

    private val _inventario = MutableStateFlow(InventarioUiState())
    val inventario: StateFlow<InventarioUiState> = _inventario

    init {
        cargarInventario()
    }

    fun cargarInventario() {
        viewModelScope.launch {
            _inventario.update { it.copy(loading = true, errorMsg = null) }
            val res = repository.getAll()
            if (res.isSuccess) {
                _inventario.update { it.copy(inventario = res.getOrNull() ?: emptyList(), loading = false) }
            } else {
                _inventario.update { it.copy(loading = false, errorMsg = res.exceptionOrNull()?.message) }
            }
        }
    }

    // ✅ Código corregido para agregarInventario:
    fun agregarInventario( titulo: String, autor: String, descripcion: String, categoria: String, precio: Int, stock: Int) {
        viewModelScope.launch {
            _inventario.update { it.copy(loading = true, errorMsg = null, successMsg = null) }

            val itemToInsert = InventarioEntity(
                titulo = titulo,
                autor = autor,
                descripcion = descripcion,
                categoria = categoria,
                precio = precio,
                stock = stock
            )

            // Llama a la inserción en el repositorio.
            val res = repository.insert(itemToInsert)

            if (res.isSuccess) {
                // Recarga el inventario completo para obtener el ítem con su ID generado.
                cargarInventario()
                _inventario.update { it.copy(loading = false, successMsg = "Item agregado exitosamente") }
            } else {
                _inventario.update { it.copy(loading = false, errorMsg = res.exceptionOrNull()?.message) }
            }
        }
    }

    // ✅ Código corregido para eliminarInventario:
    fun eliminarInventario(item: InventarioEntity) {
        viewModelScope.launch {
            _inventario.update { it.copy(loading = true, errorMsg = null, successMsg = null) }

            // El repositorio llama a la eliminación en la base de datos.
            val res = repository.delete(item)

            if (res.isSuccess) {
                // Opción 1 (Filtrado local - Más rápido, pero menos seguro si la DB falla):
                /*
                _inventario.update { current ->
                    current.copy(
                        inventario = current.inventario.filter { it.idProducto != item.idProducto },
                        loading = false,
                        successMsg = "Item eliminado correctamente"
                    )
                }
                */
                // Opción 2 (Recargar - Más seguro para asegurar sincronización con la DB):
                cargarInventario()
                _inventario.update { it.copy(loading = false, successMsg = "Item eliminado correctamente") }

            } else {
                _inventario.update { it.copy(loading = false, errorMsg = res.exceptionOrNull()?.message) }
            }
        }
    }

    fun actualizarInventario( item: InventarioEntity) {
        viewModelScope.launch {
            _inventario.update { it.copy(loading = true, errorMsg = null, successMsg = null) }

            val res = repository.update(item)
            if (res.isSuccess) {
                _inventario.update { current ->
                    current.copy(
                        inventario = current.inventario.map {
                            if (it.idProducto == item.idProducto) item else it
                        },
                        loading = false,
                        successMsg = "Item actualizado correctamente"
                    )
                }
            } else {
                _inventario.update { it.copy(loading = false, errorMsg = res.exceptionOrNull()?.message) }
            }
        }
    }

    fun reducirStock(compras: List<ItemCarrito>) {
        viewModelScope.launch {
            _inventario.update { it.copy(loading = true, errorMsg = null) }

            // Lista actualizada
            val inventarioActual = _inventario.value.inventario.toMutableList()

            compras.forEach { item ->
                val index = inventarioActual.indexOfFirst { it.idProducto == item.producto.idProducto }
                if (index != -1) {
                    val producto = inventarioActual[index]
                    val nuevoStock = (producto.stock - item.cantidad).coerceAtLeast(0)
                    inventarioActual[index] = producto.copy(stock = nuevoStock)
                }
            }

            // Actualiza la UI
            _inventario.update { it.copy(inventario = inventarioActual, loading = false) }

            //actualiza  DB
            inventarioActual.forEach { repository.update(it) }
        }
    }


    fun clearMessages() {
        _inventario.update { it.copy(errorMsg = null, successMsg = null) }
    }

}
