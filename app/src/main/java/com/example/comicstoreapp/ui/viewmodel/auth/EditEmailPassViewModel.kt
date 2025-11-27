package com.example.comicstoreapp.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.repository.UsuarioRepository
import com.example.comicstoreapp.domain.validation.confirmarContrasena
import com.example.comicstoreapp.domain.validation.validarContrasena
import com.example.comicstoreapp.domain.validation.validarCorreo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditEmailPassUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class EditEmailPassViewModel(
    private val repo: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditEmailPassUiState())
    val uiState = _uiState.asStateFlow()

    // -------------------------------
    // Inicializar email del usuario
    // -------------------------------
    fun initData(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    // -------------------------------
    // Actualizar campos
    // -------------------------------
    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            emailError = validarCorreo(value)
        )
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = validarContrasena(value),
            confirmError = confirmarContrasena(value, _uiState.value.confirmPassword)
        )
    }

    fun onConfirmChange(value: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = value,
            confirmError = confirmarContrasena(_uiState.value.password, value)
        )
    }

    // -------------------------------
    // Guardar cambios
    // -------------------------------
    fun actualizar(id: Long) {
        val state = _uiState.value

        // Validar antes de enviar
        if (state.emailError != null ||
            state.passwordError != null ||
            state.confirmError != null
        ) return

        _uiState.value = state.copy(loading = true, error = null)

        viewModelScope.launch {
            val res = repo.actualizarEmailPassword(id, state.email, state.password)

            res.fold(
                onSuccess = {
                    _uiState.value = EditEmailPassUiState(success = true)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = e.message ?: "Error al actualizar los datos"
                    )
                }
            )
        }
    }
}
