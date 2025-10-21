package com.example.comicstoreapp.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.local.user.UserEntity
import com.example.comicstoreapp.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdminUiState(
    val users: List<UserEntity> = emptyList(),
    val loading: Boolean = false,
    val errorMsg: String? = null,
    val successMsg: String? = null
)

class AdminViewModel(private val repository: UserRepository) : ViewModel() {

    private val _admin = MutableStateFlow(AdminUiState())
    val admin: StateFlow<AdminUiState> = _admin

    init {
        observeUsuarios()
    }

    private fun observeUsuarios() {
        viewModelScope.launch {
            try {
                repository.getAllUsersFlow().collect { usuarios ->
                    _admin.update { it.copy(users = usuarios, loading = false, errorMsg = null) }
                }
            } catch (e: Exception) {
                _admin.update { it.copy(loading = false, errorMsg = e.message) }
            }
        }
    }

    fun deleteUser(usuario: UserEntity) {
        viewModelScope.launch {
            _admin.update { it.copy(loading = true, errorMsg = null) }
            val res = repository.deleteUser(usuario)
            if (res.isSuccess) {
                _admin.update { it.copy(successMsg = "Usuario eliminado") }
            } else {
                _admin.update { it.copy(errorMsg = res.exceptionOrNull()?.message, loading = false) }
            }
        }
    }

    fun cambiarRol(usuarioId: Long, nuevoRol: String) {
        viewModelScope.launch {
            _admin.update { it.copy(loading = true, errorMsg = null) }
            val res = repository.updateUserRole(usuarioId, nuevoRol)
            if (res.isSuccess) {
                _admin.update { it.copy(successMsg = "Rol actualizado") }
            } else {
                _admin.update { it.copy(errorMsg = res.exceptionOrNull()?.message, loading = false) }
            }
        }
    }
}
