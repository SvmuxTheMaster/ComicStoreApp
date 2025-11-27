package com.example.comicstoreapp.ui.screen.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.comicstoreapp.domain.validation.formatearPesos
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.admin.AdminVentasViewModel
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminHistorialVentasScreen(
    navController: NavHostController,
    sessionVm: UserSessionViewModel,
    vm: AdminVentasViewModel = viewModel()
) {

    AppScaffold(
        sessionVm = sessionVm,
        navController = navController,
        onLogout = { sessionVm.cerrarSesion() }
    ) {

        val ui by vm.uiState.collectAsState()

        LaunchedEffect(Unit) {
            vm.cargarVentas()
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                "Historial de Ventas",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                "Revisa todas las ventas registradas en la tienda",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            when {
                ui.loading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
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

                ui.ventas.isEmpty() -> {
                    Text("No hay ventas registradas todavía.")
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                        items(ui.ventas) { venta ->

                            val fechaLegible = try {
                                val parsed = LocalDateTime.parse(venta.fecha)
                                parsed.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            } catch (_: Exception) {
                                venta.fecha
                            }

                            val estadoColor = when (venta.estado) {
                                "PAGADO" -> Color(0xFF0288D1)
                                "ENVIADO" -> Color(0xFF7B1FA2)
                                "ENTREGADO" -> Color(0xFF2E7D32)
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

                                    // ID y Estado
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Venta #${venta.id}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    estadoColor.copy(alpha = 0.2f),
                                                    shape = MaterialTheme.shapes.small
                                                )
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                venta.estado,
                                                color = estadoColor,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    Text("Usuario: ${venta.usuarioId}")
                                    Text("Fecha: $fechaLegible")

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        "Total: ${formatearPesos(venta.total.toString())} CLP",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(12.dp))

                                    HorizontalDivider(
                                        Modifier,
                                        DividerDefaults.Thickness,
                                        DividerDefaults.color
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    Text(
                                        "Items del pedido",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    venta.items.forEach { item ->
                                        Column(Modifier.padding(vertical = 4.dp)) {
                                            Text("• Comic ID: ${item.comicId}")
                                            Text("  Cantidad: ${item.cantidad}")
                                            Text("  Precio unitario: ${item.precioUnitario} CLP")
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
}
