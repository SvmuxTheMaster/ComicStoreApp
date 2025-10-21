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
import com.example.comicstoreapp.data.repository.InventarioRepository
import com.example.comicstoreapp.data.repository.UserRepository
import com.example.comicstoreapp.screen.seller.SellerScreen
import com.example.comicstoreapp.ui.screen.admin.GestionInventarioVm
import com.example.comicstoreapp.ui.screen.admin.GestionReportes
import com.example.comicstoreapp.ui.screen.admin.GestionUserScreenVm
import com.example.comicstoreapp.ui.screen.admin.HomeAdminScreen
import com.example.comicstoreapp.ui.screen.auth.LoginScreenVm
import com.example.comicstoreapp.ui.screen.auth.RegisterScreenVm
import com.example.comicstoreapp.ui.screen.seller.GestionarStockScreen
import com.example.comicstoreapp.ui.screen.seller.PedidosScreen
import com.example.comicstoreapp.ui.screen.user.ComicScreen
import com.example.comicstoreapp.ui.screen.user.HomeScreen
import com.example.comicstoreapp.ui.viewmodel.admin.AdminViewModel
import com.example.comicstoreapp.ui.viewmodel.admin.AdminViewModelFactory
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModelFactory
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModelFactory


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
    val inventarioDao = db.inventarioDao()


    val userRepository = UserRepository(userDao)
    val inventarioRepository = InventarioRepository(inventarioDao)


    val authViewModel: AuthViewModel = viewModel( factory = AuthViewModelFactory( userRepository ) )
    val adminViewModel: AdminViewModel = viewModel( factory = AdminViewModelFactory( userRepository ))
    val inventarioViewModel: InventarioViewModel = viewModel( factory = InventarioViewModelFactory( inventarioRepository ))


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
        composable("gestionInventario") { GestionInventarioVm (navController, authViewModel, inventarioViewModel) }
        composable("gestionUsuarios") { GestionUserScreenVm( navController, authViewModel, adminViewModel) }
        composable("verReportes") { GestionReportes(navController, authViewModel) }

        //SellerScreen
        composable("homeSeller") { SellerScreen(navController, authViewModel) }
        composable("gestionarStock") { GestionarStockScreen(navController, authViewModel) }
        composable("gestionarPedidos") { PedidosScreen(navController, authViewModel) }
    }
}
