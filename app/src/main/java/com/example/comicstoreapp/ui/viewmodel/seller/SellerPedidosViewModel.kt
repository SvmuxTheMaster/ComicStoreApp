package com.example.comicstoreapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.remote.dto.pedido.PedidoResponse
import com.example.comicstoreapp.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SellerPedidosUiState(
    val pedidos: List<PedidoResponse> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

class SellerPedidosViewModel(
    private val repo: PedidoRepository = PedidoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerPedidosUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarPedidos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)

            val result = repo.obtenerTodosLosPedidos()

            if (result.isSuccess) {
                _uiState.value = SellerPedidosUiState(
                    pedidos = result.getOrNull() ?: emptyList(),
                    loading = false
                )
            } else {
                _uiState.value = SellerPedidosUiState(
                    loading = false,
                    error = result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    fun actualizarEstado(id: Int, estado: String) {
        viewModelScope.launch {
            val result = repo.actualizarEstadoPedido(id, estado)

            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = "No se pudo actualizar el estado",
                    success = null
                )
            } else {
                // Guardamos el mensaje ANTES de recargar
                _uiState.value = _uiState.value.copy(
                    success = "Estado actualizado correctamente",
                    error = null
                )

                // Recargamos sin borrar el mensaje
                recargarPedidosSinResetear()
            }
        }
    }

    private suspend fun recargarPedidosSinResetear() {
        val result = repo.obtenerTodosLosPedidos()
        if (result.isSuccess) {
            _uiState.value = _uiState.value.copy(
                pedidos = result.getOrNull() ?: emptyList(),
                loading = false
            )
        } else {
            _uiState.value = _uiState.value.copy(
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(success = null, error = null)
    }

}
