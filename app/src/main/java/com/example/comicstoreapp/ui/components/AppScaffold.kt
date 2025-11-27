package com.example.comicstoreapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel
import com.example.comicstoreapp.ui.viewmodel.carro.CartViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    sessionVm: UserSessionViewModel,
    navController: NavHostController,
    cartVm: CartViewModel? = null,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    val usuario = sessionVm.usuario.collectAsState().value
    val uiState = cartVm?.uiState?.collectAsState()?.value
    val carrito = uiState?.items

    // Si no hay usuario, volver al login
    if (usuario == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
        return
    }

    val rol = usuario.rol.lowercase()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menuItems = when (rol) {
        "admin" -> listOf(
            "Panel de Administración" to "admin_home",
            "Gestión de productos" to "admin_products",
            "Gestión de usuarios" to "admin_users",
            "Historial de ventas" to "admin_ventas"
        )
        "vendedor" -> listOf(
            "Panel del Vendedor" to "homeSeller",
            "Pedidos" to "gestionarPedidos",
            "Gestionar stock" to "seller_inventario"
        )
        "usuario" -> listOf(
            "Inicio" to "home",
            "Ver productos" to "comics",
            "Mi Perfil" to "perfil/${usuario.id}/${usuario.nombre}/${usuario.rut}/${usuario.email}/${usuario.rol}",
            "Mi Historial de Compras" to "mis_compras"
        )
        else -> emptyList()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                Text(
                    text = "Menú",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(Modifier.height(8.dp))

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

                Spacer(Modifier.height(12.dp))

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
                    title = {
                        Text(
                            "ComicVerse",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    actions = {
                        if (rol == "usuario") {
                            val totalItems = carrito?.sumOf { it.cantidad }

                            Box(contentAlignment = Alignment.TopEnd) {

                                IconButton(onClick = {
                                    navController.navigate("carro")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Carrito",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                                if (totalItems != null) {
                                    if (totalItems > 0) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(x = (-4).dp, y = (4).dp)
                                        ) {
                                            Text(totalItems.toString())
                                        }
                                    }
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                content()
            }
        }
    }
}
