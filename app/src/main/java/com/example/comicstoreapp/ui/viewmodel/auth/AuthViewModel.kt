package com.example.comicstoreapp.ui.viewmodel.auth

import androidx.lifecycle.*
import com.example.comicstoreapp.data.local.datastore.UserPreferences
import com.example.comicstoreapp.data.repository.UserRepository
import com.example.comicstoreapp.domain.validation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val nombre: String? = null,
    val rol: String? = null,
    val correoError: String? = null,
    val contrasenaError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class RegisterUiState(
    val nombre: String = "",
    val rut: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val contrasenaConfirm: String = "",
    val nombreError: String? = null,
    val rutError: String? = null,
    val correoError: String? = null,
    val contrasenaError: String? = null,
    val contrasenaConfirmError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)


class AuthViewModel(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    // Flujos que leen directamente desde DataStore
    val isLoggedIn: StateFlow<Boolean> = userPreferences.isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )


    val userRole: StateFlow<String?> = userPreferences.userRole.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val userId: StateFlow<Long?> = userPreferences.userId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // ----------------- LOGIN -----------------

    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(correo = value, correoError = validarCorreo(value)) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(contrasena = value) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.correoError == null &&
                s.correo.isNotBlank() &&
                s.contrasena.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(400)

            val result = repository.login(s.correo.trim(), s.contrasena.trim())

            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    userPreferences.saveUser(user.idUsuario, user.correo, user.nombre, user.rol)
                }
            }

            _login.update {
                if (result.isSuccess) {
                    it.copy(
                        isSubmitting = false,
                        success = true,
                        errorMsg = null,
                        nombre = result.getOrNull()?.nombre,
                        rol = result.getOrNull()?.rol
                    )
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación"
                    )
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- REGISTRO -----------------

    fun onNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update { it.copy(nombre = filtered, nombreError = validarNombre(filtered)) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(correo = value, correoError = validarCorreo(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onRutChange(value: String) {
        _register.update { it.copy(rut = value, rutError = validarRut(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update {
            it.copy(contrasena = value, contrasenaError = validarContrasena(value))
        }
        _register.update {
            it.copy(contrasenaConfirmError = confirmarContrasena(it.contrasena, it.contrasenaConfirm))
        }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update {
            it.copy(
                contrasenaConfirm = value,
                contrasenaConfirmError = confirmarContrasena(it.contrasena, value)
            )
        }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(
            s.nombreError, s.correoError, s.rutError,
            s.contrasenaError, s.contrasenaConfirmError
        ).all { it == null }
        val filled = s.nombre.isNotBlank() && s.correo.isNotBlank() &&
                s.rut.isNotBlank() && s.contrasena.isNotBlank() &&
                s.contrasenaConfirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(600)

            val result = repository.register(
                s.nombre.trim(),
                s.correo.trim(),
                s.rut.trim(),
                s.contrasena.trim(),
                "usuario"
            )

            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar"
                    )
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- LOGOUT -----------------

    fun onLogOut() {
        viewModelScope.launch {
            userPreferences.clearUser()
        }

        _login.update {
            it.copy(
                correo = "",
                contrasena = "",
                nombre = null,
                rol = null,
                success = false,
                errorMsg = null
            )
        }

        _register.update {
            it.copy(
                nombre = "",
                rut = "",
                correo = "",
                contrasena = "",
                success = false,
                errorMsg = null
            )
        }
    }
}