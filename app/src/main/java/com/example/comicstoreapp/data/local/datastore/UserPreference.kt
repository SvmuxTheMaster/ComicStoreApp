package com.example.comicstoreapp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {

    companion object {

        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_NOMBRE = stringPreferencesKey("nombre")
        private val KEY_ROL = stringPreferencesKey("rol")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val userRole: Flow<String?> = context.dataStore.data.map { it[KEY_ROL] }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_LOGGED_IN] ?: false }

    val userId: Flow<Long?> = context.dataStore.data.map { prefs -> prefs[KEY_USER_ID]?.toLongOrNull()}


        suspend fun saveUser(id: Long, email: String, nombre: String, rol: String) {
            context.dataStore.edit { prefs ->
                prefs[KEY_USER_ID] = id.toString()
                prefs[KEY_EMAIL] = email
                prefs[KEY_NOMBRE] = nombre
                prefs[KEY_ROL] = rol
                prefs[KEY_IS_LOGGED_IN] = true
            }
        }

        suspend fun clearUser() {
            context.dataStore.edit { prefs ->
                prefs.clear()
            }
        }
    }

