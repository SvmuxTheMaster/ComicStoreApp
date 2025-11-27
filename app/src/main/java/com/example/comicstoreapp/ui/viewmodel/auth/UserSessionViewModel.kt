package com.example.comicstoreapp.ui.viewmodel.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.comicstoreapp.data.local.UserPreferences
import com.example.comicstoreapp.data.local.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserSessionViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreferences(application)

    private val _usuario = MutableStateFlow<UserSession?>(null)
    val usuario: StateFlow<UserSession?> = _usuario

    init {
        // Se ejecuta al iniciar la app
        cargarSesion()
    }

    private fun cargarSesion() {
        viewModelScope.launch {
            prefs.userFlow.collect { user ->
                _usuario.value = user
            }
        }
    }

    // Guardar sesión luego del login
    fun guardarSesion(
        id: Long,
        nombre: String,
        email: String,
        rut: String?,
        rol: String
    ) {
        viewModelScope.launch {
            prefs.saveUser(id, nombre, email, rut?: "", rol)
        }
    }

    // Cerrar sesión
    fun cerrarSesion() {
        viewModelScope.launch {
            prefs.logout()
            _usuario.value = null
        }
    }
}