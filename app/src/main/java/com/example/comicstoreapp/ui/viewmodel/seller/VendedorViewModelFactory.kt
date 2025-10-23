package com.example.comicstoreapp.ui.viewmodel.seller


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.comicstoreapp.data.repository.InventarioRepository
import com.example.comicstoreapp.data.repository.PedidoRepository


class VendedorViewModelFactory(
    private val inventarioRepository: InventarioRepository,
    private val pedidoRepository: PedidoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VendedorViewModel::class.java)) {
            return VendedorViewModel(inventarioRepository, pedidoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
