package com.example.comicstoreapp.ui.screen.user


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import coil.compose.rememberAsyncImagePainter
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.carro.CarritoViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    authVm: AuthViewModel,
    inventarioVm: InventarioViewModel,
    carritoVm: CarritoViewModel
) {
    val role = authVm.userRole.collectAsState().value ?: ""
    val inventarioState by inventarioVm.inventario.collectAsState()

    AppScaffold(
        rol = role,
        navController = navController,
        onLogout = {
            authVm.onLogOut()
            navController.navigate("login") { popUpTo("0") { inclusive = true } }
        },
        carritoVm = carritoVm
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Bienvenido a ComicVerse", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Poductos Recomendados", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(inventarioState.inventario) { comic ->
                    ComicCard(comic = comic)
                }
            }
        }
    }
}

@Composable
fun ComicCard(comic: InventarioEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            comic.fotoUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = comic.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(comic.titulo, style = MaterialTheme.typography.titleSmall)
            Text("$${comic.precio}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}