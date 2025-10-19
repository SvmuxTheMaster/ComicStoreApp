package com.example.comicstoreapp.ui.screen.user


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.comicstoreapp.R
import com.example.comicstoreapp.ui.components.ComicCard
import com.example.comicstoreapp.ui.viewmodel.AuthViewModel

@Composable
fun ComicScreen(navController: NavHostController, vm: AuthViewModel,) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            ComicCard(
                title = "Batman #1",
                price = "5.990",
                stock = 10,
                imageRes = R.drawable.dc_logo
            )
        }
    }
}
