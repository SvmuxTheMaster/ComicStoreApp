package com.example.comicstoreapp.ui.screen.seller

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.comicstoreapp.data.local.pedido.PedidoEntity
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.seller.VendedorViewModel


@Composable
fun PedidosScreenVm(
    navController: NavHostController,
    authVm: AuthViewModel,
    vendedorVm: VendedorViewModel
) {
    val state by vendedorVm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vendedorVm.cargarPedidos()
    }

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

    PedidosScreen(
        pedidos = state.pedido,
        loading = state.loading,
        onActualizarEstado = { pedido, estado -> vendedorVm.actualizarEstadoPedido(pedido, estado) },
        onEliminarPedido = { pedido -> vendedorVm.eliminarPedido(pedido) },
        navController = navController,
        authVm = authVm
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    pedidos: List<PedidoEntity>,
    loading: Boolean,
    onActualizarEstado: (PedidoEntity, String) -> Unit,
    onEliminarPedido: (PedidoEntity) -> Unit,
    navController: NavHostController,
    authVm: AuthViewModel
) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                pedidos.isEmpty() -> {
                    Text(
                        text = "No hay pedidos disponibles.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pedidos) { pedido ->
                            PedidoCard(
                                pedido = pedido,
                                onActualizarEstado = onActualizarEstado,
                                onEliminarPedido = onEliminarPedido
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoCard(
    pedido: PedidoEntity,
    onActualizarEstado: (PedidoEntity, String) -> Unit,
    onEliminarPedido: (PedidoEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Pedido #${pedido.idPedido}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Usuario: ${pedido.idusuario}", style = MaterialTheme.typography.bodyMedium)
            Text("Fecha: ${pedido.fecha}", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(8.dp))

            Text(
                "Estado actual: ${pedido.estado.uppercase()}",
                color = when (pedido.estado) {
                    "pendiente" -> MaterialTheme.colorScheme.error
                    "en proceso" -> MaterialTheme.colorScheme.primary
                    "entregado" -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onActualizarEstado(pedido, "en proceso") },
                    enabled = pedido.estado == "pendiente",
                ) {
                    Text("En proceso")
                }

                Button(
                    onClick = { onActualizarEstado(pedido, "entregado") },
                    enabled = pedido.estado != "entregado"
                ) {
                    Text("Entregado")
                }

                IconButton(onClick = { onEliminarPedido(pedido) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Animación sutil al completar estado
            AnimatedVisibility(
                visible = pedido.estado == "entregado",
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "✅ Pedido entregado con éxito",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}