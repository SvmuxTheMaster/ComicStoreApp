package com.example.comicstoreapp.ui.viewmodel.admin


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioDto
import com.example.comicstoreapp.data.repository.UsuarioAdminRepository
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdminUsersUiState(
    val loading: Boolean = false,
    val usuarios: List<UsuarioDto> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class AdminUsersViewModel(
    private val repo: UsuarioAdminRepository = UsuarioAdminRepository(),
    private val sessionVm: UserSessionViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsersUiState())
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    private fun rolHeader(): String {
        val rol = sessionVm.usuario.value?.rol?.trim()?.lowercase() ?: ""
        println("DEBUG ROL HEADER = '$rol'")
        return rol
    }


    fun cargarUsuarios() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null, successMessage = null) }
            val result = repo.listarUsuarios(rolHeader())
            result.onSuccess { list ->
                _uiState.update { it.copy(loading = false, usuarios = list) }
            }.onFailure { e ->
                _uiState.update { it.copy(loading = false, error = e.message ?: "Error") }
            }
        }
    }

    fun cambiarRol(usuarioId: Long, nuevoRol: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null, successMessage = null) }

            val result = repo.actualizarRol(rolHeader(), usuarioId, nuevoRol)

            result.onSuccess {

                // Recargar usuarios
                val listaActualizada = repo.listarUsuarios(rolHeader())

                listaActualizada.onSuccess { users ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            usuarios = users,
                            successMessage = "Rol actualizado correctamente"
                        )
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = e.message ?: "Error al refrescar lista"
                        )
                    }
                }

            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = e.message ?: "No se pudo actualizar"
                    )
                }
            }
        }
    }



    fun eliminarUsuario(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null, successMessage = null) }
            val result = repo.eliminarUsuario(rolHeader(), id)
            result.onSuccess {
                _uiState.update { state ->
                    val filtered = state.usuarios.filterNot { it.id == id }
                    state.copy(loading = false, usuarios = filtered, successMessage = "Usuario eliminado")
                }
            }.onFailure { e ->
                _uiState.update { it.copy(loading = false, error = e.message ?: "No se pudo eliminar") }
            }
        }
    }

}
