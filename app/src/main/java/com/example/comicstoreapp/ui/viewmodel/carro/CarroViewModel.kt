package com.example.comicstoreapp.ui.viewmodel.carro

import androidx.lifecycle.ViewModel
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Modelo de carrito
data class ItemCarrito(
    val producto: InventarioEntity,
    val cantidad: Int
)

class CarritoViewModel : ViewModel() {

    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito

    fun agregarProducto(producto: InventarioEntity) {
        val lista = _carrito.value.toMutableList()
        val existente = lista.find { it.producto.idProducto == producto.idProducto }
        if (existente != null) {
            lista[lista.indexOf(existente)] = existente.copy(cantidad = existente.cantidad + 1)
        } else {
            lista.add(ItemCarrito(producto = producto, cantidad = 1))
        }
        _carrito.value = lista
    }

    fun disminuirCantidad(idProducto: Long) {
        val lista = _carrito.value.toMutableList()
        val existente = lista.find { it.producto.idProducto == idProducto } ?: return
        if (existente.cantidad > 1) {
            lista[lista.indexOf(existente)] = existente.copy(cantidad = existente.cantidad - 1)
        } else {
            lista.remove(existente)
        }
        _carrito.value = lista
    }

    fun limpiarCarrito() {
        _carrito.value = emptyList()
    }

    fun total(): Int = _carrito.value.sumOf { it.producto.precio * it.cantidad }
}
