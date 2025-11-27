package com.example.comicstoreapp.ui.viewmodel.carro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.remote.dto.comic.ComicDto
import com.example.comicstoreapp.data.remote.dto.pedido.PedidoItemRequest
import com.example.comicstoreapp.data.remote.dto.pedido.PedidoRequest
import com.example.comicstoreapp.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartItem(
    val comic: ComicDto,
    val cantidad: Int
)

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val total: Int = 0,
    val loading: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

class CartViewModel(
    private val repo: PedidoRepository = PedidoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    fun agregarAlCarrito(comic: ComicDto) {
        val actual = _uiState.value
        val lista = actual.items.toMutableList()

        val index = lista.indexOfFirst { it.comic.id == comic.id }

        if (index >= 0) {
            val item = lista[index]
            lista[index] = item.copy(cantidad = item.cantidad + 1)
        } else {
            lista.add(CartItem(comic, 1))
        }

        _uiState.value = actual.copy(
            items = lista,
            total = calcularTotal(lista),
            error = null,
            success = null
        )
    }

    fun quitarUnidad(comicId: Int) {
        val actual = _uiState.value
        val lista = actual.items.toMutableList()

        val index = lista.indexOfFirst { it.comic.id == comicId }
        if (index >= 0) {
            val item = lista[index]
            if (item.cantidad > 1) {
                lista[index] = item.copy(cantidad = item.cantidad - 1)
            } else {
                lista.removeAt(index)
            }

            _uiState.value = actual.copy(
                items = lista,
                total = calcularTotal(lista),
                error = null,
                success = null
            )
        }
    }

    fun eliminarItem(comicId: Int) {
        val actual = _uiState.value
        val lista = actual.items.filter { it.comic.id != comicId }

        _uiState.value = actual.copy(
            items = lista,
            total = calcularTotal(lista),
            error = null,
            success = null
        )
    }

    private fun calcularTotal(items: List<CartItem>): Int {
        return items.sumOf { it.comic.precio * it.cantidad }
    }

    fun confirmarPedido(usuarioId: Int) {
        val actual = _uiState.value

        if (actual.items.isEmpty()) {
            _uiState.value = actual.copy(error = "El carrito está vacío")
            return
        }

        val body = PedidoRequest(
            usuarioId = usuarioId,
            items = actual.items.map { cartItem ->
                val id = cartItem.comic.id ?: throw IllegalStateException("comic.id es null en ${cartItem.comic.titulo}")

                PedidoItemRequest(
                    comicId = id,
                    cantidad = cartItem.cantidad
                )
            }
        )

        _uiState.value = actual.copy(loading = true, error = null, success = null)

        viewModelScope.launch {
            val result = repo.crearPedido(body)

            result.fold(
                onSuccess = {
                    _uiState.value = CartUiState(
                        items = emptyList(),
                        total = 0,
                        loading = false,
                        error = null,
                        success = "Su pedido ha sido creado correctamente"
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = e.message ?: "Error al crear el pedido",
                        success = null
                    )
                }
            )
        }
    }
}