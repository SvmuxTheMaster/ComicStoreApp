package com.example.comicstoreapp.ui.screen.admin

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.AuthViewModel

@Composable
fun GestionUserScreen(navController: NavHostController, vm: AuthViewModel){

    AppScaffold(
        rol = "admin",
        navController = navController,
        onLogout = {
            vm.onLogOut()
            navController.navigate("login"){ popUpTo("0") { inclusive = true } }
        }
    ){

        Text("Bienvenido administrador a la gestion de usuarios ")

    }

}