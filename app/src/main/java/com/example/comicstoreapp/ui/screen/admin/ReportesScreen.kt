package com.example.comicstoreapp.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GestionReportes(
    onVolverClick: () -> Unit // Solo la función de callback para la acción.
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Ilustración/Ícono para comunicar el error
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = "Contenido no encontrado",
            tint = MaterialTheme.colorScheme.error, // Usamos el color principal de la marca
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.height(32.dp))

        // 2. Título (404)
        Text(
            text = "404",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        // 3. Mensaje amigable
        Text(
            text = "¡Vaya! Parece que la página o el recurso que buscas no existe.",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(48.dp))

        // 4. Llamada a la Acción (CTA)
        Button(
            onClick = onVolverClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a la Pantalla Principal")
        }
    }
}

// Bloque de Preview para ver el diseño en Android Studio
@Preview(showBackground = true)
@Composable
fun PreviewError404UI() {
    MaterialTheme { // Envuelve con tu tema para ver los colores correctos
        GestionReportes(onVolverClick = {})
    }
}