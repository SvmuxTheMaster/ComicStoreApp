package com.example.comicstoreapp.ui.screen.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.comicstoreapp.domain.validation.formatearPesos
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel
import com.example.comicstoreapp.ui.viewmodel.user.UserComprasViewModel

@Composable
fun UserComprasScreen(
    navController: NavHostController,
    sessionVm: UserSessionViewModel,
    vm: UserComprasViewModel = viewModel()
) {

    AppScaffold(
        sessionVm = sessionVm,
        navController = navController,
        onLogout = { sessionVm.cerrarSesion() }
    ) {

        val usuario = sessionVm.usuario.collectAsState().value!!
        val ui by vm.ui.collectAsState()

        LaunchedEffect(Unit) {
            vm.cargarCompras(usuario.id.toInt())
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                "Mis Compras",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            if (ui.loading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
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

            if (ui.compras.isEmpty()) {
                Text("Aún no has realizado compras.")
                return@AppScaffold
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(ui.compras) { compra ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {

                        Column(Modifier.padding(16.dp)) {

                            Text(
                                "Pedido #${compra.id}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )

                            Spacer(Modifier.height(6.dp))

                            Text("Fecha: ${compra.fecha}")
                            Text("Estado: ${compra.estado}")

                            Spacer(Modifier.height(6.dp))

                            Text(
                                "Total: ${formatearPesos(compra.total.toString())} CLP",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(6.dp))

                            compra.items.forEach { item ->
                                Text("- Comic ID: ${item.comicId} | x${item.cantidad}")
                            }
                        }
                    }
                }
            }
        }
    }
}
