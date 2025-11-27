package com.example.comicstoreapp.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.repository.UsuarioRepository
import com.example.comicstoreapp.domain.validation.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val nombre: String = "",
    val rut: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Errores por campo
    val errorNombre: String? = null,
    val errorRut: String? = null,
    val errorEmail: String? = null,
    val errorPassword: String? = null,
    val errorConfirmPassword: String? = null,

    val loading: Boolean = false,
    val success: Boolean = false,

    val errorGeneral: String? = null // error del servidor
)


class RegisterViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()


    // CAMBIO DE CAMPOS + reset errores
    fun onNombreChange(text: String) {
        _uiState.update { it.copy(nombre = text, errorNombre = null) }
    }

    fun onRutChange(text: String) {
        _uiState.update { it.copy(rut = text, errorRut = null) }
    }

    fun onEmailChange(text: String) {
        _uiState.update { it.copy(email = text, errorEmail = null) }
    }

    fun onPasswordChange(text: String) {
        _uiState.update { it.copy(password = text, errorPassword = null) }
    }

    fun onConfirmPasswordChange(text: String) {
        _uiState.update { it.copy(confirmPassword = text, errorConfirmPassword = null) }
    }


    // FUNCIÓN DE REGISTRO
    fun register() {
        val state = _uiState.value

        // VALIDACIONES
        val errorNombre = validarNombre(state.nombre)
        val errorRut = validarRut(state.rut)
        val errorEmail = validarCorreo(state.email)
        val errorPassword = validarContrasena(state.password)
        val errorConfirm = confirmarContrasena(state.password, state.confirmPassword)

        // Si hay errores, mostramos y detenemos
        if (errorNombre != null ||
            errorRut != null ||
            errorEmail != null ||
            errorPassword != null ||
            errorConfirm != null
        ) {
            _uiState.update {
                it.copy(
                    errorNombre = errorNombre,
                    errorRut = errorRut,
                    errorEmail = errorEmail,
                    errorPassword = errorPassword,
                    errorConfirmPassword = errorConfirm
                )
            }
            return
        }

        //llamado al microservicio
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorGeneral = null) }

            val result = repository.register(
                nombre = state.nombre,
                rut = state.rut,
                email = state.email,
                password = state.password
            )

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        loading = false,
                        success = true
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        errorGeneral = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(success = false) }
    }
}