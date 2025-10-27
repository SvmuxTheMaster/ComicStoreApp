package com.example.comicstoreapp.ui.screen.seller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel


@Composable
fun SellerScreen(
    navController: NavHostController,
    authVm: AuthViewModel,
    inventarioVm: InventarioViewModel
) {
    val role = authVm.userRole.collectAsState().value ?: ""
    val inventarioState by inventarioVm.inventario.collectAsState()

    AppScaffold(
        rol = role,
        navController = navController,
        onLogout = {
            authVm.onLogOut()
            navController.navigate("login") { popUpTo("0") { inclusive = true } }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("👋 Bienvenido, Vendedor", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            Text("📦 Resumen de inventario", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Productos disponibles: ${inventarioState.inventario.size}")
                    val stockBajo = inventarioState.inventario.count { it.stock < 5 }
                    Text("Stock bajo (<5): $stockBajo")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("🛒 Accesos rápidos", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { navController.navigate("gestionInventario") }) {
                Text("Gestionar Inventario")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { /* navController.navigate("ventas") */ }) {
                Text("Registrar Venta")
            }
        }
    }
}