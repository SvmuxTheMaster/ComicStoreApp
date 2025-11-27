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
import com.example.comicstoreapp.ui.viewmodel.seller.SellerInventarioViewModel


@Composable
fun SellerInventarioScreen(
    navController: NavHostController,
    sessionVm: UserSessionViewModel,
    vm: SellerInventarioViewModel = viewModel()
) {

    AppScaffold(
        sessionVm = sessionVm,
        navController = navController,
        onLogout = { sessionVm.cerrarSesion() }
    ) {

        val ui by vm.ui.collectAsState()

        LaunchedEffect(Unit) { vm.cargarInventario() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                "Gestión de Inventario",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Aquí puedes ajustar el stock de cada producto.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            when {
                ui.loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                ui.error != null -> {
                    Text(
                        ui.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(ui.comics) { comic ->

                            val isCritico = comic.cantidad <= 2
                            val estadoColor = if (isCritico) Color(0xFFD32F2F) else Color(0xFF388E3C)
                            val estadoTexto = if (isCritico) "Stock Crítico" else "Stock Disponible"

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(Modifier.padding(16.dp)) {

                                    Text(
                                        comic.titulo,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        "ID PRODUCTO: ${comic.id}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {

                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = estadoColor.copy(alpha = 0.15f),
                                                    shape = MaterialTheme.shapes.small
                                                )
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                estadoTexto,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = estadoColor
                                            )
                                        }

                                        Text(
                                            "Stock actual: ${comic.cantidad}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    var nuevoStock by remember { mutableStateOf(comic.cantidad.toString()) }

                                    OutlinedTextField(
                                        value = nuevoStock,
                                        onValueChange = { if (it.all(Char::isDigit)) nuevoStock = it },
                                        label = { Text("Nuevo stock") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            if (nuevoStock.isNotBlank()) {
                                                vm.actualizarStock(comic.id, nuevoStock.toInt())
                                            }
                                        }
                                    ) {
                                        Text("Actualizar Stock")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
