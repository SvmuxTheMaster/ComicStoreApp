package com.example.comicstoreapp.ui.screen.seller

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel

@Composable
fun PedidosScreen(navController: NavHostController, vm: AuthViewModel) {
    AppScaffold(
        rol = "vendedor",
        navController = navController,
        onLogout = {
            vm.onLogOut()
            navController.navigate("login"){
                popUpTo("0") { inclusive = true }
            }
        }
    ) {

    }
}