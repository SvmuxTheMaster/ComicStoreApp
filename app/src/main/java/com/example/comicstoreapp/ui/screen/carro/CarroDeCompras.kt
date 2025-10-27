package com.example.comicstoreapp.ui.screen.carro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.carro.CarritoViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel
import com.example.comicstoreapp.ui.viewmodel.seller.VendedorViewModel

@Composable
fun CarroDeComprasScreen(
    navController: NavHostController,
    authVm: AuthViewModel,
    carritoVm: CarritoViewModel,
    inventarioVm: InventarioViewModel,
    vendedorVm: VendedorViewModel,
    onFinalizarCompra: () -> Unit
) {
    val carrito by carritoVm.carrito.collectAsState()


    AppScaffold(
        rol = authVm.userRole.collectAsState().value ?: "",
        navController = navController,
        onLogout = {
            authVm.onLogOut()
            navController.navigate("login") { popUpTo("0") { inclusive = true } }
        },
        carritoVm = carritoVm
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Carrito de Compras", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            if (carrito.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu carrito está vacío 🛒")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(carrito) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = item.producto.titulo,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(text = "Cantidad: ${item.cantidad}")
                                    Text(text = "Precio: $${item.producto.precio}")
                                }

                                Text(
                                    text = "Subtotal: $${item.producto.precio * item.cantidad}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Total: $${carritoVm.total()}",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        inventarioVm.reducirStock(carrito)  // Actualiza stock
                        val usuarioId = authVm.userId.value ?: 0L
                        vendedorVm.crearPedido(usuarioId, carrito)
                        carritoVm.limpiarCarrito()
                        onFinalizarCompra()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar Compra")
                }
            }
        }
    }
}
