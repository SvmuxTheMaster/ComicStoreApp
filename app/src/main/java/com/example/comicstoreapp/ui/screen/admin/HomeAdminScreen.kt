package com.example.comicstoreapp.ui.screen.admin


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel
import com.example.comicstoreapp.ui.viewmodel.admin.AdminViewModel


@Composable
fun HomeAdminScreen(
    navController: NavHostController,
    authVm: AuthViewModel,
    adminVm: AdminViewModel,
    inventarioVm: InventarioViewModel
) {
    val role = authVm.userRole.collectAsState().value ?: ""
    val users = adminVm.admin.collectAsState()
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
            Text("👋 Bienvenido, Administrador", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            Text("📊 Resumen general", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Usuarios registrados: ${users.value.users.size}")
                    Text("Productos en inventario: ${inventarioState.inventario.size}")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("🔧 Accesos rápidos", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { navController.navigate("gestionUsuarios") }) {
                    Text("👥 Gestionar Usuarios")
                }
                Button(onClick = { navController.navigate("gestionInventario") }) {
                    Text("📦 Inventario")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = { navController.navigate("verReportes") }) {
                Text("📈 Ver Reportes")
            }
        }
    }
}

