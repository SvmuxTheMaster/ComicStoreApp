package com.example.comicstoreapp

import androidx.navigation.compose.NavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.comicstoreapp.data.local.database.AppDatabase
import com.example.comicstoreapp.data.repository.UserRepository
import com.example.comicstoreapp.screen.seller.SellerScreen
import com.example.comicstoreapp.ui.screen.admin.GestionInventarioScreen
import com.example.comicstoreapp.ui.screen.admin.GestionReportes
import com.example.comicstoreapp.ui.screen.admin.GestionUserScreen
import com.example.comicstoreapp.ui.screen.admin.HomeAdminScreen
import com.example.comicstoreapp.ui.screen.auth.LoginScreenVm
import com.example.comicstoreapp.ui.screen.auth.RegisterScreenVm
import com.example.comicstoreapp.ui.screen.seller.GestionarStockScreen
import com.example.comicstoreapp.ui.screen.seller.PedidosScreen
import com.example.comicstoreapp.ui.screen.user.ComicScreen
import com.example.comicstoreapp.ui.screen.user.HomeScreen
import com.example.comicstoreapp.ui.viewmodel.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.AuthViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()
    val userRepository = UserRepository(userDao)
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        //AuthScreen
        composable("login") { LoginScreenVm(navController, authViewModel) }
        composable("register") { RegisterScreenVm(navController, authViewModel) }

        //UserScreen
        composable("home") { HomeScreen(navController, authViewModel) }
        composable("comics") { ComicScreen(navController, authViewModel) }

        //AdminScreen
        composable("homeAdmin") { HomeAdminScreen(navController, authViewModel) }
        composable("gestionInventario") { GestionInventarioScreen(navController, authViewModel) }
        composable("gestionUsuarios") { GestionUserScreen(navController, authViewModel) }
        composable("verReportes") { GestionReportes(navController, authViewModel) }

        //SellerScreen
        composable("homeSeller") { SellerScreen(navController, authViewModel) }
        composable("gestionarStock") { GestionarStockScreen(navController, authViewModel) }
        composable("gestionarPedidos") { PedidosScreen(navController, authViewModel) }
    }
}
