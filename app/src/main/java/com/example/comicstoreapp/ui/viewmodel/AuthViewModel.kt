package com.example.comicstoreapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.domain.validation.confirmarContrasena
import com.example.comicstoreapp.domain.validation.validarContrasena
import com.example.comicstoreapp.domain.validation.validarCorreo
import com.example.comicstoreapp.domain.validation.validarNombre
import com.example.comicstoreapp.domain.validation.validarRut
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",


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

private data class DemoUser(
    val nombre: String,
    val rut: String,
    val correo: String,
    val contrasena: String,

)

class AuthViewModel : ViewModel() {                         // ViewModel que maneja Login/Registro

    // Colección **estática** en memoria compartida entre instancias del VM (sin storage persistente)
    companion object {
        // Lista mutable de usuarios para la demo (se pierde al cerrar la app)
        private val USERS = mutableListOf(
            // Usuario por defecto para probar login:
            DemoUser(nombre = "Demo", correo = "a@a.cl", rut = "12345678", contrasena = "Demo123!")
        )
    }

    // Flujos de estado para observar desde la UI
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

    fun submitLogin() {                                     // Acción de login (simulación async)
        val s = _login.value                                // Snapshot del estado
        if (!s.canSubmit || s.isSubmitting) return          // Si no se puede o ya está cargando, salimos
        viewModelScope.launch {                             // Lanzamos corrutina
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) } // Seteamos loading
            delay(500)                                      // Simulamos tiempo de verificación

            // Buscamos en la **colección en memoria** un usuario con ese email
            val user = USERS.firstOrNull { it.correo.equals(s.correo, ignoreCase = true) }

            // ¿Coincide email + contraseña?
            val ok = user != null && user.contrasena == s.contrasena

            _login.update {                                 // Actualizamos con el resultado
                it.copy(
                    isSubmitting = false,                   // Fin carga
                    success = ok,                           // true si credenciales correctas
                    errorMsg = if (!ok) "Credenciales inválidas" else null // Mensaje si falla
                )
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

    fun onPhoneChange(value: String) {                      // Handler del teléfono
        val digitsOnly = value.filter { it.isDigit() }      // Dejamos solo dígitos
        _register.update {                                  // Guardamos + validamos
            it.copy(rut = digitsOnly, rutError = validarRut(digitsOnly))
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

            // ¿Existe ya un usuario con el mismo email en la **colección**?
            val duplicated = USERS.any { it.correo.equals(s.correo, ignoreCase = true) }

            if (duplicated) {                               // Si ya existe, devolvemos error
                _register.update {
                    it.copy(isSubmitting = false, success = false, errorMsg = "El usuario ya existe")
                }
                return@launch                                // Salimos
            }

            // Insertamos el nuevo usuario en la **colección** (solo demo; no persistimos)
            USERS.add(
                DemoUser(
                    nombre = s.nombre.trim(),
                    correo = s.correo.trim(),
                    rut = s.rut.trim(),
                    contrasena = s.contrasena                            // En demo lo guardamos en texto (para clase)
                )
            )

            _register.update {                               // Éxito
                it.copy(isSubmitting = false, success = true, errorMsg = null)
            }
        }
    }

    fun clearRegisterResult() {                             // Limpia banderas tras navegar
        _register.update { it.copy(success = false, errorMsg = null) }
    }
}