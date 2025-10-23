package com.example.comicstoreapp.screen.seller

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel


@Composable
fun SellerScreen(navController: NavHostController, authVm: AuthViewModel){

    AppScaffold(
        rol = authVm.userRole.collectAsState().value ?: "",
        navController = navController,
        onLogout = {
            authVm.onLogOut()
            navController.navigate("login"){ popUpTo("0") { inclusive = true } }
        }
    ){

        Text("Bienvenido vendedor ")

    }
}

