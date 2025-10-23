package com.example.comicstoreapp.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.comicstoreapp.data.local.datastore.UserPreferences
import com.example.comicstoreapp.data.repository.UserRepository


class AuthViewModelFactory(
    private val repository: UserRepository,
    private val userPrefs: UserPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository, userPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}