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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.AdminEditComicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditComicScreen(
    navController: NavHostController,
    sessionVm: UserSessionViewModel,
    comicId: Int,
    vm: AdminEditComicViewModel = viewModel()
) {
    val ui = vm.uiState.value
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Cargar comic al entrar
    LaunchedEffect(comicId) {
        vm.cargarComic(comicId)
    }

    // Picker de imagen
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) vm.uploadImage(context, uri)
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

            Text("Editar Producto", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            // Loading inicial
            if (ui.loading && ui.id == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@AppScaffold
            }

            // Errors / Success
            ui.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(10.dp))
            }

            ui.success?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(10.dp))
            }

            // --- CAMPOS ---
            OutlinedTextField(
                value = ui.titulo,
                onValueChange = vm::onTituloChange,
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = ui.descripcion,
                onValueChange = vm::onDescripcionChange,
                label = { Text("Descripción") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = ui.autor,
                onValueChange = vm::onAutorChange,
                label = { Text("Autor") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = ui.precio,
                onValueChange = vm::onPrecioChange,
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = ui.cantidad,
                onValueChange = vm::onCantidadChange,
                label = { Text("Cantidad disponible") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // --- IMAGEN ---
            OutlinedTextField(
                value = ui.imagenUrl,
                onValueChange = {},
                label = { Text("URL imagen") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { imagePicker.launch("image/*") },
                enabled = !ui.uploadingImage,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (ui.uploadingImage) "Subiendo imagen..." else "Cambiar imagen")
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

            // --- CATEGORÍA ---
            var expanded by remember { mutableStateOf(false) }
            val categorias = listOf("Marvel", "Dc", "Manga")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = ui.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                vm.onCategoriaChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(25.dp))

            // --- BOTONES ---
            Row {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) { Text("Cancelar") }

                Spacer(Modifier.width(10.dp))

                Button(
                    onClick = { vm.actualizarComic { navController.popBackStack() } },
                    enabled = !ui.loading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (ui.loading) "Guardando..." else "Guardar")
                }
            }
        }
    }
}
