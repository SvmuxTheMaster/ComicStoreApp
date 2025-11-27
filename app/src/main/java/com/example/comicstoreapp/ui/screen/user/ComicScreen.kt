package com.example.comicstoreapp.ui.screen.user



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.comicstoreapp.data.remote.dto.comic.ComicDto
import com.example.comicstoreapp.domain.validation.formatearPesos
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel
import com.example.comicstoreapp.ui.viewmodel.carro.CartViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel

@Composable
fun ComicScreen(
    navController: NavHostController,
    sessionVm: UserSessionViewModel,
    cartVm: CartViewModel,
    inventarioViewModel: InventarioViewModel = viewModel()
) {
    val uiState = inventarioViewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) { inventarioViewModel.cargarInventario() }

    AppScaffold(
        cartVm = cartVm,
        sessionVm = sessionVm,
        navController = navController,
        onLogout = {
            sessionVm.cerrarSesion()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                "Catálogo de Cómics",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            when {
                uiState.loading -> LoadingSection()
                uiState.error != null -> ErrorSection(uiState.error)
                uiState.comics.isEmpty() -> EmptySection()
                else -> ComicList(
                    comics = uiState.comics,
                    onClick = {
                        navController.navigate("comicDetail/${it.id}")
                    }
                )
            }
        }
    }
}


@Composable
fun LoadingSection() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorSection(msg: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Error: $msg", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun EmptySection() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No hay cómics disponibles.")
    }
}

@Composable
fun ComicList(comics: List<ComicDto>, onClick: (ComicDto) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(comics) { comic ->
            ComicCard(comic = comic, onClick = { onClick(comic) })
        }
    }
}

@Composable
fun ComicCard(comic: ComicDto, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            // Imagen simple y fija (evita deformaciones)
            AsyncImage(
                model = comic.imagenUrl ?: "",
                contentDescription = comic.titulo,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                // Título
                Text(
                    text = comic.titulo,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Autor
                Text(
                    text = comic.autor,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )

                Spacer(Modifier.height(6.dp))

                // Categoría
                Text(
                    text = "Categoría: ${comic.categoria}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                // Stock (simple)
                Text(
                    text = "Stock: ${comic.cantidad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(Modifier.height(6.dp))

                // Precio
                Text(
                    text = "CLP ${formatearPesos(comic.precio.toString())}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}