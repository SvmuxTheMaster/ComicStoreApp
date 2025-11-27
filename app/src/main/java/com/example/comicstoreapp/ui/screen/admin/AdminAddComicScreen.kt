package com.example.comicstoreapp.ui.screen.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.AdminAddComicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddComicScreen(
    navController: NavHostController,
    sessionVm: UserSessionViewModel,
    vm: AdminAddComicViewModel = viewModel()
) {
    val ui = vm.uiState.value
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Image picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            vm.uploadImage(context, uri)
        }
    }

    AppScaffold(
        sessionVm = sessionVm,
        navController = navController,
        onLogout = {
            sessionVm.cerrarSesion()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(scrollState)
        ) {

            Text("Agregar Nuevo Cómic", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            ui.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(10.dp))
            }

            ui.success?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(10.dp))
            }

            // TÍTULO
            OutlinedTextField(
                value = ui.titulo,
                onValueChange = vm::onTituloChange,
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // DESCRIPCIÓN
            OutlinedTextField(
                value = ui.descripcion,
                onValueChange = vm::onDescripcionChange,
                label = { Text("Descripción") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // AUTOR
            OutlinedTextField(
                value = ui.autor,
                onValueChange = vm::onAutorChange,
                label = { Text("Autor") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // PRECIO
            OutlinedTextField(
                value = ui.precio,
                onValueChange = vm::onPrecioChange,
                label = { Text("Precio (CLP)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // CANTIDAD
            OutlinedTextField(
                value = ui.cantidad,
                onValueChange = vm::onCantidadChange,
                label = { Text("Cantidad en stock") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // IMAGEN URL (solo lectura)
            OutlinedTextField(
                value = ui.imagenUrl,
                onValueChange = {},
                readOnly = true,
                label = { Text("URL de imagen (servidor)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // BOTÓN IMAGEN
            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !ui.uploadingImage
            ) {
                Text(if (ui.uploadingImage) "Subiendo imagen..." else "Seleccionar imagen")
            }

            if (ui.imagenUrl.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                AsyncImage(
                    model = ui.imagenUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // CATEGORÍA
            var categoriaExpanded by remember { mutableStateOf(false) }
            val categorias = listOf("Marvel", "Dc", "Manga")

            ExposedDropdownMenuBox(
                expanded = categoriaExpanded,
                onExpandedChange = { categoriaExpanded = !categoriaExpanded }
            ) {
                OutlinedTextField(
                    value = ui.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = categoriaExpanded,
                    onDismissRequest = { categoriaExpanded = false }
                ) {
                    categorias.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                vm.onCategoriaChange(option)
                                categoriaExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // BOTONES
            Row {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Spacer(Modifier.width(10.dp))

                Button(
                    onClick = {
                        vm.guardarComic { navController.popBackStack() }
                    },
                    enabled = !ui.loading && !ui.uploadingImage,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (ui.loading) "Guardando..." else "Guardar")
                }
            }
        }
    }
}
