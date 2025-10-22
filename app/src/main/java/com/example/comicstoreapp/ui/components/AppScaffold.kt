package com.example.comicstoreapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    rol: String,
    navController: NavHostController,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Menú dinámico según rol
    val menuItems = when (rol) {
        "admin" -> listOf(
            "Control inventario" to "gestionInventario",
            "Gestionar usuarios" to "gestionUsuarios",
            "Ver reportes" to "verReportes",
        )
        "vendedor" -> listOf(
            "Pedidos" to "gestionarPedidos",
            "Gestionar stock" to "gestionarStock"
        )
        "usuario" -> listOf( // usuario
            "Ver productos" to "comics",
            "Historial de compras" to "historial"
        )
        else -> emptyList()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú (${rol})", // borrar el rol despues
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                // Opciones dinámicas
                menuItems.forEach { (title, route) ->
                    Text(
                        text = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(route)
                                scope.launch { drawerState.close() }
                            }
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(Modifier.padding(vertical = 8.dp))

                // Opción de cerrar sesión
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onLogout()
                            scope.launch { drawerState.close() }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    Text("Cerrar sesión", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ComicVerse") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            },
            content = { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    content()
                }
            }
        )
    }
}