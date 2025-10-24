package com.example.comicstoreapp.ui.screen.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel
import java.io.File


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

            //mostrar imagen en gestion de inventario
            item.fotoUri?.let { uriString ->
                val uri = Uri.parse(uriString)
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Foto del cómic",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 8.dp)
                )
            }

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
    val context = LocalContext.current
    var titulo by remember { mutableStateOf(productoInicial?.titulo ?: "") }
    var autor by remember { mutableStateOf(productoInicial?.autor ?: "") }
    var descripcion by remember { mutableStateOf(productoInicial?.descripcion ?: "") }
    var categoria by remember { mutableStateOf(productoInicial?.categoria ?: "") }
    var precio by remember { mutableStateOf(productoInicial?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(productoInicial?.stock?.toString() ?: "") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher de cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Toast.makeText(context, "Foto tomada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No se tomó la foto", Toast.LENGTH_SHORT).show()
            fotoUri = null
        }
    }

    // Launcher para pedir permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Crear archivo solo después de permiso
            val file = File(context.cacheDir, "comic_${System.currentTimeMillis()}.jpg")
            file.parentFile?.mkdirs()

            // URI local
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            // Guardar en estado para mostrar en UI
            fotoUri = uri

            // Usar la variable local, no la propiedad delegada
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }


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

                Spacer(modifier = Modifier.height(8.dp))

                // Botón para tomar foto
                Button(onClick = {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }) {
                    Text("Tomar foto del cómic")
                }

                // Mostrar imagen tomada
                fotoUri?.let { uri ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Foto del cómic",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
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
                            stock = stock.toInt(),
                            fotoUri = fotoUri?.toString()
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
