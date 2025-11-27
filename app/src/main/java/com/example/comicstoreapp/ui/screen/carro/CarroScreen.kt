package com.example.comicstoreapp.ui.screen.carro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.comicstoreapp.domain.validation.formatearPesos
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel
import com.example.comicstoreapp.ui.viewmodel.carro.CartViewModel

@Composable
fun CarroScreen(
    navController: NavHostController,
    sessionVm: UserSessionViewModel,
    cartVm: CartViewModel
) {
    val ui by cartVm.uiState.collectAsState()
    val usuario = sessionVm.usuario.collectAsState().value
    val usuarioId = (usuario?.id ?: 0L).toInt()

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

            // TÍTULO
            Text(
                "Tu Carrito",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ERRORES / ÉXITOS
            ui.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(8.dp))
            }

            ui.success?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(8.dp))
            }

            // CARRITO VACÍO
            if (ui.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Tu carrito está vacío",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {

                // LISTA DE ITEMS
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(ui.items) { item ->

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // 📘 Imagen
                                AsyncImage(
                                    model = item.comic.imagenUrl,
                                    contentDescription = item.comic.titulo,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        item.comic.titulo,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    Text(
                                        "Subtotal: CLP ${formatearPesos((item.comic.precio * item.cantidad).toString())}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(Modifier.height(10.dp))

                                    // CONTROLES DE CANTIDAD
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        FilledTonalButton(
                                            onClick = { cartVm.quitarUnidad(item.comic.id) },
                                            shape = CircleShape,
                                            contentPadding = PaddingValues(0.dp),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Text("-", style = MaterialTheme.typography.titleMedium)
                                        }

                                        Spacer(Modifier.width(12.dp))

                                        Text(
                                            item.cantidad.toString(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(Modifier.width(12.dp))

                                        FilledTonalButton(
                                            onClick = { cartVm.agregarAlCarrito(item.comic) },
                                            shape = CircleShape,
                                            contentPadding = PaddingValues(0.dp),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Text("+", style = MaterialTheme.typography.titleMedium)
                                        }
                                    }
                                }

                                Spacer(Modifier.width(10.dp))

                                // 🗑️ ELIMINAR
                                IconButton(
                                    onClick = { cartVm.eliminarItem(item.comic.id) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            MaterialTheme.colorScheme.errorContainer,
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // TOTAL + BOTÓN
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            "Total:",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            "CLP ${formatearPesos(ui.total.toString())}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { cartVm.confirmarPedido(usuarioId) },
                            enabled = !ui.loading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (ui.loading) "Procesando..." else "Confirmar compra",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}