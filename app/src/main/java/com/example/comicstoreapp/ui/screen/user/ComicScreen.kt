package com.example.comicstoreapp.ui.screen.user


import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.comicstoreapp.data.local.inventario.InventarioEntity
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.carro.CarritoViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel

@Composable
fun ComicScreen(
    navController: NavHostController,
    authVm: AuthViewModel,
    inventarioVm: InventarioViewModel,
    carritoVm: CarritoViewModel
) {
    val inventarioState = inventarioVm.inventario.collectAsState().value


    AppScaffold(
        rol = authVm.userRole.collectAsState().value ?: "",
        navController = navController,
        onLogout = {
            authVm.onLogOut()
            navController.navigate("login") { popUpTo("0") { inclusive = true } }
        },
        carritoVm = carritoVm
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
                        ComicItemCard(item, carritoVm)
                    }
                }
            }
        }
    }
}

@Composable
fun ComicItemCard(item: InventarioEntity, carritoVm: CarritoViewModel) {

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            item.fotoUri?.let { uriString ->
                val uri = Uri.parse(uriString)
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Foto del cómic",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 8.dp)
                )
            }

            Text("Título: ${item.titulo}", style = MaterialTheme.typography.titleMedium)
            Text("Autor: ${item.autor}", style = MaterialTheme.typography.bodyMedium)
            Text("Categoría: ${item.categoria}", style = MaterialTheme.typography.bodySmall)
            Text("Precio: $${item.precio}", style = MaterialTheme.typography.bodyMedium)
            Text("Stock disponible: ${item.stock}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        carritoVm.agregarProducto(item) // <-- agregar al carrito
                        Toast.makeText(
                            context,
                            "${item.titulo} agregado al carrito",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Comprar")
                }
            }
        }
    }
}