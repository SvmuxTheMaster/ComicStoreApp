package com.example.comicstoreapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.comicstoreapp.data.repository.InventarioRepository

class VendedorViewModelFactory(
    private val repository: InventarioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VendedorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VendedorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}