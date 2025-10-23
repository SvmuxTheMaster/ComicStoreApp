package com.example.comicstoreapp

import androidx.navigation.compose.NavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.comicstoreapp.data.local.database.AppDatabase
import com.example.comicstoreapp.data.local.datastore.UserPreferences
import com.example.comicstoreapp.data.repository.InventarioRepository
import com.example.comicstoreapp.data.repository.PedidoRepository
import com.example.comicstoreapp.data.repository.UserRepository
import com.example.comicstoreapp.screen.seller.SellerScreen
import com.example.comicstoreapp.ui.screen.admin.GestionInventarioVm
import com.example.comicstoreapp.ui.screen.admin.GestionReportes
import com.example.comicstoreapp.ui.screen.admin.GestionUserScreenVm
import com.example.comicstoreapp.ui.screen.admin.HomeAdminScreen
import com.example.comicstoreapp.ui.screen.auth.LoginScreenVm
import com.example.comicstoreapp.ui.screen.auth.RegisterScreenVm
import com.example.comicstoreapp.ui.screen.seller.GestionarStockScreenVm
import com.example.comicstoreapp.ui.screen.seller.PedidosScreenVm
import com.example.comicstoreapp.ui.screen.user.ComicScreen
import com.example.comicstoreapp.ui.screen.user.HomeScreen
import com.example.comicstoreapp.ui.viewmodel.admin.AdminViewModel
import com.example.comicstoreapp.ui.viewmodel.admin.AdminViewModelFactory
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModelFactory
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModelFactory
import com.example.comicstoreapp.ui.viewmodel.seller.VendedorViewModel
import com.example.comicstoreapp.ui.viewmodel.seller.VendedorViewModelFactory


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
    val context = LocalContext.current
    val userPrefs = UserPreferences(context)
    val db = AppDatabase.getInstance(context)

    // DAOs
    val userDao = db.userDao()
    val inventarioDao = db.inventarioDao()
    val pedidoDao = db.pedidoDao()

    // Repositorios
    val userRepository = UserRepository(userDao)
    val inventarioRepository = InventarioRepository(inventarioDao)
    val pedidoRepository = PedidoRepository(pedidoDao)

    // ViewModels
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(userRepository, userPrefs))
    val adminViewModel: AdminViewModel = viewModel(factory = AdminViewModelFactory(userRepository))
    val inventarioViewModel: InventarioViewModel = viewModel(factory = InventarioViewModelFactory(inventarioRepository))
    val vendedorViewModel: VendedorViewModel = viewModel(factory = VendedorViewModelFactory(inventarioRepository, pedidoRepository))

    // NavController
    val navController = rememberNavController()

    // 🔥 Leemos el estado de login y rol en tiempo real
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()

    // 🔄 Redirección automática según estado de sesión
    LaunchedEffect(isLoggedIn, userRole) {
        when {
            !isLoggedIn -> {
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
            userRole == "admin" -> {
                navController.navigate("homeAdmin") {
                    popUpTo(0)
                }
            }
            userRole == "vendedor" -> {
                navController.navigate("homeSeller") {
                    popUpTo(0)
                }
            }
            else -> {
                navController.navigate("home") {
                    popUpTo(0)
                }
            }
        }
    }

    //Estructura de navegación
    NavHost(navController = navController, startDestination = "login") {

        // Auth
        composable("login") { LoginScreenVm(navController, authViewModel) }
        composable("register") { RegisterScreenVm(navController, authViewModel) }

        // Cliente
        composable("home") { HomeScreen(navController, authViewModel) }
        composable("comics") { ComicScreen(navController, authViewModel) }

        // Admin
        composable("homeAdmin") { HomeAdminScreen(navController, authViewModel) }
        composable("gestionInventario") { GestionInventarioVm(navController, authViewModel, inventarioViewModel) }
        composable("gestionUsuarios") { GestionUserScreenVm(navController, authViewModel, adminViewModel) }
        composable("verReportes") { GestionReportes(navController, authViewModel) }

        // Vendedor
        composable("homeSeller") { SellerScreen(navController, authViewModel) }
        composable("gestionarStock") { GestionarStockScreenVm(navController, authViewModel, vendedorViewModel) }
        composable("gestionarPedidos") { PedidosScreenVm(navController, authViewModel, vendedorViewModel) }
    }
}
