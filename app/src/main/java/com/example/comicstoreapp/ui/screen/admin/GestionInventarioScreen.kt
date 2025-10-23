package com.example.comicstoreapp.ui.screen.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel

@Composable
fun GestionInventarioVm(
    navController: NavHostController,
    authVm: AuthViewModel,
    inventarioVm: InventarioViewModel
) {
    val inventarioState by inventarioVm.inventario.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(inventarioState.successMsg, inventarioState.errorMsg) {
        inventarioState.successMsg?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        inventarioState.errorMsg?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        inventarioVm.clearMessages()
    }

    GestionInventarioScreen(
        inventario = inventarioState.inventario,
        isLoading = inventarioState.loading,
        onDeleteItem = { inventarioVm.eliminarInventario(it) },
        onUpdateItem = { inventarioVm.actualizarInventario(it) },
        onAddItem = { inventarioVm.agregarInventario(it.titulo, it.autor, it.descripcion, it.categoria, it.precio, it.stock) },
        navController = navController,
        authVm = authVm
    )
}

@Composable
fun GestionInventarioScreen(
    inventario: List<InventarioEntity>,
    isLoading: Boolean,
    onDeleteItem: (InventarioEntity) -> Unit,
    onUpdateItem: (InventarioEntity) -> Unit,
    onAddItem: (InventarioEntity) -> Unit,
    navController: NavHostController,
    authVm: AuthViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<InventarioEntity?>(null) }
    var deleteItem by remember { mutableStateOf<InventarioEntity?>(null) }

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
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Gestión de Inventario", style = MaterialTheme.typography.titleLarge)
                Button(onClick = { showAddDialog = true }) {
                    Text("Agregar producto")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (inventario.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos en el inventario")
                }
            } else {
                LazyColumn {
                    items(inventario.size) { index ->
                        InventarioItemCard(
                            item = inventario[index],
                            onEdit = { editItem = it },
                            onDelete = { deleteItem = it }
                        )
                    }
                }
            }
        }

        // 🟢 Dialog para agregar producto
        if (showAddDialog) {
            InventarioDialog(
                title = "Agregar nuevo producto",
                onDismiss = { showAddDialog = false },
                onConfirm = { nuevo ->
                    onAddItem(nuevo)
                    showAddDialog = false
                }
            )
        }

        // 🟡 Dialog para editar producto
        editItem?.let { item ->
            InventarioDialog(
                title = "Editar producto",
                productoInicial = item,
                onDismiss = { editItem = null },
                onConfirm = { actualizado ->
                    onUpdateItem(actualizado.copy(idProducto = item.idProducto))
                    editItem = null
                }
            )
        }

        // 🔴 Confirmación de eliminación
        deleteItem?.let { item ->
            AlertDialog(
                onDismissRequest = { deleteItem = null },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteItem(item)
                        deleteItem = null
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteItem = null }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Deseas eliminar \"${item.titulo}\" del inventario?") }
            )
        }
    }
}

@Composable
fun InventarioItemCard(
    item: InventarioEntity,
    onEdit: (InventarioEntity) -> Unit,
    onDelete: (InventarioEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Título: ${item.titulo}")
            Text("Autor: ${item.autor}")
            Text("Categoría: ${item.categoria}")
            Text("Precio: $${item.precio}")
            Text("Stock: ${item.stock}")

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                TextButton(onClick = { onEdit(item) }) { Text("Editar") }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { onDelete(item) }) { Text("Eliminar") }
            }
        }
    }
}

@Composable
fun InventarioDialog(
    title: String,
    productoInicial: InventarioEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (InventarioEntity) -> Unit
) {
    var titulo by remember { mutableStateOf(productoInicial?.titulo ?: "") }
    var autor by remember { mutableStateOf(productoInicial?.autor ?: "") }
    var descripcion by remember { mutableStateOf(productoInicial?.descripcion ?: "") }
    var categoria by remember { mutableStateOf(productoInicial?.categoria ?: "") }
    var precio by remember { mutableStateOf(productoInicial?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(productoInicial?.stock?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") })
                OutlinedTextField(value = autor, onValueChange = { autor = it }, label = { Text("Autor") })
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
                OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") })
                Row {
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it.filter { c -> c.isDigit() } },
                        label = { Text("Precio") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it.filter { c -> c.isDigit() } },
                        label = { Text("Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (titulo.isNotBlank() && precio.isNotBlank() && stock.isNotBlank()) {
                    onConfirm(
                        InventarioEntity(
                            titulo = titulo,
                            autor = autor,
                            descripcion = descripcion,
                            categoria = categoria,
                            precio = precio.toInt(),
                            stock = stock.toInt()
                        )
                    )
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
