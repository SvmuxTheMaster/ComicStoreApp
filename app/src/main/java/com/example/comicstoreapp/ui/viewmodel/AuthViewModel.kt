package com.example.comicstoreapp.ui.viewmodel

import androidx.lifecycle.*
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


class AuthViewModel ( private val repository: UserRepository): ViewModel() {
    private val _login = MutableStateFlow(LoginUiState())   // Estado interno (Login)
    val login: StateFlow<LoginUiState> = _login             // Exposición inmutable

    private val _register = MutableStateFlow(RegisterUiState()) // Estado interno (Registro)
    val register: StateFlow<RegisterUiState> = _register        // Exposición inmutable

    // ----------------- LOGIN: handlers y envío -----------------

    fun onLoginEmailChange(value: String) {                 // Handler cuando cambia el email
        _login.update { it.copy(correo = value, correoError = validarCorreo(value)) } // Guardamos + validamos
        recomputeLoginCanSubmit()                           // Recalculamos habilitado
    }

    fun onLoginPassChange(value: String) {                  // Handler cuando cambia la contraseña
        _login.update { it.copy(contrasena = value) }             // Guardamos (sin validar fuerza aquí)
        recomputeLoginCanSubmit()                           // Recalculamos habilitado
    }

    private fun recomputeLoginCanSubmit() {                 // Regla para habilitar botón "Entrar"
        val s = _login.value                                // Tomamos el estado actual
        val can = s.correoError == null &&                   // Email válido
                s.correo.isNotBlank() &&                   // Email no vacío
                s.contrasena.isNotBlank()                       // Password no vacía
        _login.update { it.copy(canSubmit = can) }          // Actualizamos el flag
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return


        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(500)

            // Buscamos en la **colección en memoria** un usuario con ese email
            val result = repository.login(s.correo.trim(), s.contrasena.trim())


            // Interpreta el resultado y actualiza estado
            _login.update {
                if (result.isSuccess) {
                    it.copy(
                        isSubmitting = false,
                        success = true,
                        errorMsg = null,
                        nombre = result.getOrNull()?.nombre,
                        rol = result.getOrNull()?.rol
                    ) // OK: éxito
                } else {
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación")
                }
            }
        }
    }

    fun clearLoginResult() {                                // Limpia banderas tras navegar
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- REGISTRO: handlers y envío -----------------

    fun onNameChange(value: String) {                       // Handler del nombre
        val filtered = value.filter { it.isLetter() || it.isWhitespace() } // Filtramos números/símbolos (solo letras/espacios)
        _register.update {                                  // Guardamos + validamos
            it.copy(nombre = filtered, nombreError = validarNombre(filtered))
        }
        recomputeRegisterCanSubmit()                        // Recalculamos habilitado
    }

    fun onRegisterEmailChange(value: String) {              // Handler del email
        _register.update { it.copy(correo = value, correoError = validarCorreo(value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }

    fun onRutChange(value: String) {                      // Handler del teléfono
        _register.update {                                  // Guardamos + validamos
            it.copy(rut = value, rutError = validarRut(value))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {               // Handler de la contraseña
        _register.update { it.copy(contrasena = value, contrasenaError = validarContrasena(value)) } // Validamos seguridad
        // Revalidamos confirmación con la nueva contraseña
        _register.update { it.copy(contrasenaConfirmError = confirmarContrasena(it.contrasena, it.contrasenaConfirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {                    // Handler de confirmación
        _register.update { it.copy(contrasenaConfirm = value, contrasenaConfirmError = confirmarContrasena(it.contrasena, value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {              // Habilitar "Registrar" si todo OK
        val s = _register.value                              // Tomamos el estado actual
        val noErrors = listOf(s.nombreError, s.correoError, s.rutError, s.contrasenaError, s.contrasenaConfirmError).all { it == null } // Sin errores
        val filled = s.nombre.isNotBlank() && s.correo.isNotBlank() && s.rut.isNotBlank() && s.contrasena.isNotBlank() && s.contrasenaConfirm.isNotBlank() // Todo lleno
        _register.update { it.copy(canSubmit = noErrors && filled) } // Actualizamos flag
    }

    fun submitRegister() {                                  // Acción de registro (simulación async)
        val s = _register.value                              // Snapshot del estado
        if (!s.canSubmit || s.isSubmitting) return          // Evitamos reentradas
        viewModelScope.launch {                             // Corrutina
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) } // Loading
            delay(700)                                      // Simulamos IO


            val result = repository.register(
                s.nombre.trim(),
                s.correo.trim(),
                s.rut.trim(),
                s.contrasena.trim(),
                "usuario"
            )

            // Interpreta resultado y actualiza estado
            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)  // OK
                } else {
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar")
                }
            }
        }
    }

    fun clearRegisterResult() {                             // Limpia banderas tras navegar
        _register.update { it.copy(success = false, errorMsg = null) }
    }
}