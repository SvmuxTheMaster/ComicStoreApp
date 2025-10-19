package com.example.comicstoreapp.ui.screen.user


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.AuthViewModel

@Composable
fun HomeScreen(navController: NavHostController, vm: AuthViewModel,) {
    AppScaffold(
        rol = "usuario",
        navController = navController,
        onLogout = {
            vm.onLogOut()
            navController.navigate("login"){
                popUpTo("0") { inclusive = true }
            }
        }
    ){

    }
}
