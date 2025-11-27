package com.example.comicstoreapp.data.local


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow


val Context.userDataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_ID = longPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_RUT = stringPreferencesKey("user_rut")
        private val USER_ROL = stringPreferencesKey("user_rol")
    }

    // Guardar usuario
    suspend fun saveUser(id: Long, nombre: String, email: String, rut: String, rol: String) {
        context.userDataStore.edit { prefs ->
            prefs[USER_ID] = id
            prefs[USER_NAME] = nombre
            prefs[USER_EMAIL] = email
            prefs[USER_RUT] = rut
            prefs[USER_ROL] = rol
        }
    }

    // Leer usuario
    val userFlow: Flow<UserSession?> = context.userDataStore.data.map { prefs ->
        val id = prefs[USER_ID] ?: return@map null
        UserSession(
            id = id,
            nombre = prefs[USER_NAME] ?: "",
            email = prefs[USER_EMAIL] ?: "",
            rut = prefs[USER_RUT] ?: "",
            rol = prefs[USER_ROL] ?: ""
        )
    }

    // Cerrar sesión
    suspend fun logout() {
        context.userDataStore.edit { it.clear() }
    }
}

// Modelo de sesión usada en el ViewModel
data class UserSession(
    val id: Long,
    val nombre: String,
    val email: String,
    val rut: String,
    val rol: String
)
