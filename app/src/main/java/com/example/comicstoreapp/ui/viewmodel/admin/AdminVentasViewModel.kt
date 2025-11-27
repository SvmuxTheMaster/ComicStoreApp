package com.example.comicstoreapp.ui.viewmodel.admin


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.remote.dto.pedido.PedidoResponse
import com.example.comicstoreapp.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminVentasUiState(
    val ventas: List<PedidoResponse> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class AdminVentasViewModel(
    private val repo: PedidoRepository = PedidoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminVentasUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarVentas() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)

            val result = repo.obtenerTodosLosPedidos()

            if (result.isSuccess) {

                val ventasFiltradas = result.getOrNull()
                    ?.filter {
                        it.estado == "PAGADO" ||
                                it.estado == "ENVIADO" ||
                                it.estado == "ENTREGADO"
                    } ?: emptyList()

                _uiState.value = AdminVentasUiState(
                    ventas = ventasFiltradas,
                    loading = false
                )
            } else {
                _uiState.value = AdminVentasUiState(
                    loading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
}
