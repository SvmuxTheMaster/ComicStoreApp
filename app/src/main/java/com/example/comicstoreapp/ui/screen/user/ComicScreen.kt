package com.example.comicstoreapp.ui.screen.user



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel

@Composable
fun ComicScreen(
    navController: NavHostController,
    authVm: AuthViewModel,
    inventarioVm: InventarioViewModel
) {
    val inventarioState = inventarioVm.inventario.collectAsState().value

    AppScaffold(
        rol = authVm.userRole.collectAsState().value ?: "usuario",
        navController = navController,
        onLogout = {
            authVm.onLogOut()
            navController.navigate("login") {
                popUpTo("0") { inclusive = true }
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Catálogo de Comics", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            if (inventarioState.loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (inventarioState.inventario.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay comics disponibles")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(inventarioState.inventario) { item ->
                        ComicItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun ComicItemCard(item: InventarioEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Título: ${item.titulo}", style = MaterialTheme.typography.titleMedium)
            Text("Autor: ${item.autor}", style = MaterialTheme.typography.bodyMedium)
            Text("Categoría: ${item.categoria}", style = MaterialTheme.typography.bodySmall)
            Text("Precio: $${item.precio}", style = MaterialTheme.typography.bodyMedium)
            Text("Stock disponible: ${item.stock}", style = MaterialTheme.typography.bodySmall)
        }
    }
}