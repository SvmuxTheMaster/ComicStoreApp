package com.example.comicstoreapp.ui.screen.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel
import com.example.comicstoreapp.ui.viewmodel.seller.SellerPedidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerPedidosManagementScreen(
    navController: NavHostController,
    sessionVm: UserSessionViewModel,
    vm: SellerPedidosViewModel = viewModel()
) {

    AppScaffold(
        sessionVm = sessionVm,
        navController = navController,
        onLogout = { sessionVm.cerrarSesion() }
    ) {

        val ui by vm.uiState.collectAsState()

        // Cargar pedidos al entrar
        LaunchedEffect(Unit) {
            vm.cargarPedidos()
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                "Gestión de Pedidos",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(10.dp))

            if (ui.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@AppScaffold
            }

            ui.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                return@AppScaffold
            }

            ui.success?.let {
                Text(
                    it,
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (ui.pedidos.isEmpty()) {
                Text("No hay pedidos aún.")
                return@AppScaffold
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                items(ui.pedidos) { pedido ->

                    val estados = listOf("PENDIENTE", "PREPARANDO", "ENVIADO", "ENTREGADO", "CANCELADO")

                    var expanded by remember { mutableStateOf(false) }
                    var estadoActual by remember { mutableStateOf(pedido.estado) }

                    val estadoColor = when (estadoActual) {
                        "PENDIENTE" -> Color(0xFFFFA000)
                        "PREPARANDO" -> Color(0xFF0288D1)
                        "ENVIADO" -> Color(0xFF7B1FA2)
                        "ENTREGADO" -> Color(0xFF2E7D32)
                        "CANCELADO" -> Color(0xFFD32F2F)
                        else -> Color.Gray
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {

                        Column(Modifier.padding(16.dp)) {

                            Text(
                                "Pedido #${pedido.id}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )

                            Spacer(Modifier.height(4.dp))

                            Text("Usuario ID: ${pedido.usuarioId}")

                            Spacer(Modifier.height(4.dp))

                            // Estado
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = estadoColor.copy(alpha = 0.2f),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    estadoActual,
                                    color = estadoColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            // Precio total
                            Text("Total: ${pedido.total} CLP", fontWeight = FontWeight.SemiBold)

                            Spacer(Modifier.height(12.dp))

                            // Dropdown para cambiar estado
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {

                                OutlinedTextField(
                                    value = estadoActual,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Cambiar estado") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {

                                    estados.forEach { estado ->

                                        DropdownMenuItem(
                                            text = { Text(estado) },
                                            onClick = {
                                                estadoActual = estado
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            Button(
                                onClick = { vm.actualizarEstado(pedido.id, estadoActual)
                                          vm.limpiarMensajes()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Actualizar Estado")
                            }
                        }
                    }
                }
            }
        }
    }
}
