package com.example.comicstoreapp.ui.screen.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Categorías de Cómics")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("dc_comics") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("DC Comics")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("marvel_comics") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Marvel Comics")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("manga") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mangas")
        }
    }
}
