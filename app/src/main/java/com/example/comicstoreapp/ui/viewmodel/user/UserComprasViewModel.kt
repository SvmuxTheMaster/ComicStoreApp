package com.example.comicstoreapp.ui.viewmodel.user


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.remote.dto.pedido.PedidoResponse
import com.example.comicstoreapp.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserComprasUiState(
    val compras: List<PedidoResponse> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class UserComprasViewModel(
    private val repo: PedidoRepository = PedidoRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(UserComprasUiState())
    val ui = _ui.asStateFlow()

    fun cargarCompras(usuarioId: Int) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)

            val result = repo.obtenerPedidosUsuario(usuarioId)

            if (result.isSuccess) {
                _ui.value = UserComprasUiState(
                    compras = result.getOrNull() ?: emptyList()
                )
            } else {
                _ui.value = UserComprasUiState(
                    error = result.exceptionOrNull()?.message ?: "Error al obtener compras"
                )
            }
        }
    }
}
