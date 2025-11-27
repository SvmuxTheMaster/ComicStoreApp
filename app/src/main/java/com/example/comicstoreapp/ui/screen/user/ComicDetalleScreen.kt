package com.example.comicstoreapp.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.comicstoreapp.data.remote.dto.comic.ComicDto
import com.example.comicstoreapp.ui.viewmodel.carro.CartViewModel

@Composable
fun ComicDetailScreen(
    comic: ComicDto,
    cartVm: CartViewModel
) {

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .background(MaterialTheme.colorScheme.surface)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
        ) {

            AsyncImage(
                model = comic.imagenUrl,
                contentDescription = comic.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            )


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                            startY = 180f
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .offset(y = (-26).dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(20.dp)
        ) {


            Text(
                text = comic.titulo,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Autor: ${comic.autor}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))


            AssistChip(
                onClick = {},
                label = {
                    Text(
                        comic.categoria,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                shape = RoundedCornerShape(50),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            Spacer(Modifier.height(22.dp))


            Text(
                text = "$${comic.precio}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(22.dp))


            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = comic.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }

        Spacer(Modifier.height(30.dp))


        Button(
            onClick = { cartVm.agregarAlCarrito(comic) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(58.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.AddShoppingCart, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Agregar al carrito",
                style = MaterialTheme.typography.titleMedium
            )
        }


        Spacer(Modifier.height(30.dp))
    }
}
