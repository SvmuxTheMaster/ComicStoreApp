package com.example.comicstoreapp.ui.screen.admin


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.comicstoreapp.theme.ComicStoreAppTheme

@Preview(showBackground = true)
@Composable
fun HomeAdminScreen(
    navController: NavHostController? = null // valor por defecto para Preview
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Panel de Administrador",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController?.navigate("manageUsers") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gestionar Usuarios")
            }

            Button(
                onClick = { navController?.navigate("viewReports") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Reportes de Ventas")
            }

            Button(
                onClick = { navController?.navigate("manageInventory") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Controlar Inventario General")
            }
        }
    }
}
