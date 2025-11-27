package com.example.comicstoreapp.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.comicstoreapp.data.repository.UsuarioAdminRepository
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel

@Suppress("UNCHECKED_CAST")
class AdminUsersViewModelFactory(
    private val sessionVm: UserSessionViewModel,
    private val repo: UsuarioAdminRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminUsersViewModel::class.java)) {
            return AdminUsersViewModel(repo, sessionVm) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

