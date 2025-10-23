package com.example.comicstoreapp.ui.screen.seller

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.seller.VendedorViewModel


@Composable
fun GestionarStockScreenVm(
    navController: NavHostController,
    authVm: AuthViewModel,
    vendedorVm: VendedorViewModel
) {
    val state by vendedorVm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Mostrar mensajes tipo Toast
    LaunchedEffect(state.successMsg, state.errorMsg) {
        state.successMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vendedorVm.clearMessages()
        }
        state.errorMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vendedorVm.clearMessages()
        }
    }

    GestionarStockScreen(
        inventario = state.inventario,
        loading = state.loading,
        onBuscar = vendedorVm::buscarProducto,
        onActualizarStock = vendedorVm::actualizarStock,
        onEliminarProducto = vendedorVm::eliminarProducto,
        navController = navController,
        authVm = authVm
    )
}



@Composable
fun GestionarStockScreen(
    navController: NavHostController,
    authVm: AuthViewModel,
    inventario: List<InventarioEntity>,
    loading: Boolean,
    onBuscar: (String) -> Unit,
    onActualizarStock: (InventarioEntity, Int) -> Unit,
    onEliminarProducto: (InventarioEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    AppScaffold(
        rol = authVm.userRole.collectAsState().value ?: "",
        navController = navController,
        onLogout = {
            authVm.onLogOut()
            navController.navigate("login") {
                popUpTo("0") { inclusive = true }
            }
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onBuscar(it.text)
                    },
                    label = { Text("Buscar producto...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(inventario) { producto ->
                            ProductoCard(
                                producto = producto,
                                onActualizarStock = { nuevoStock ->
                                    onActualizarStock(producto, nuevoStock)
                                },
                                onEliminarProducto = {
                                    onEliminarProducto(producto)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoCard(
    producto: InventarioEntity,
    onActualizarStock: (Int) -> Unit,
    onEliminarProducto: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = producto.titulo, style = MaterialTheme.typography.titleMedium)
            Text(text = "ID: ${producto.idProducto}")
            Text(text = "Categoría: ${producto.categoria}")
            Text(text = "Precio: $${producto.precio}")
            Text(text = "Stock: ${producto.stock}")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar stock")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }

    if (showEditDialog) {
        EditStockDialog(
            stockActual = producto.stock,
            onConfirm = {
                onActualizarStock(it)
                showEditDialog = false
            },
            onCancel = { showEditDialog = false }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar producto") },
            text = { Text("¿Seguro que deseas eliminar este producto?") },
            confirmButton = {
                TextButton(onClick = {
                    onEliminarProducto()
                    showDeleteDialog = false
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun EditStockDialog(
    stockActual: Int,
    onConfirm: (Int) -> Unit,
    onCancel: () -> Unit
) {
    var nuevoStock by remember { mutableStateOf(stockActual.toString()) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Editar stock") },
        text = {
            OutlinedTextField(
                value = nuevoStock,
                onValueChange = { nuevoStock = it },
                label = { Text("Nuevo stock") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                nuevoStock.toIntOrNull()?.let { onConfirm(it) }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancelar") }
        }
    )
}
