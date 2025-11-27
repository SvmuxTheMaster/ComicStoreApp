package com.example.comicstoreapp

import android.annotation.SuppressLint
import android.os.Build
import androidx.navigation.compose.NavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioDto
import com.example.comicstoreapp.ui.screen.admin.AdminAddComicScreen
import com.example.comicstoreapp.ui.screen.admin.AdminEditComicScreen
import com.example.comicstoreapp.ui.screen.admin.AdminHistorialVentasScreen
import com.example.comicstoreapp.ui.screen.admin.AdminHomeScreen
import com.example.comicstoreapp.ui.screen.admin.AdminProductManagementScreen
import com.example.comicstoreapp.ui.screen.admin.AdminUserManagementScreen
import com.example.comicstoreapp.ui.screen.auth.LoginScreen
import com.example.comicstoreapp.ui.screen.auth.RegisterScreen
import com.example.comicstoreapp.ui.screen.carro.CarroScreen
import com.example.comicstoreapp.ui.screen.seller.SellerInventarioScreen
import com.example.comicstoreapp.ui.screen.user.ComicDetailScreen
import com.example.comicstoreapp.ui.screen.user.ComicScreen
import com.example.comicstoreapp.ui.screen.user.EditEmailPasswordScreen
import com.example.comicstoreapp.ui.screen.user.HomeScreen
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel
import com.example.comicstoreapp.ui.viewmodel.auth.LoginViewModel
import com.example.comicstoreapp.ui.viewmodel.auth.RegisterViewModel
import com.example.comicstoreapp.ui.viewmodel.carro.CartViewModel
import com.example.comicstoreapp.ui.viewmodel.inventario.InventarioViewModel
import com.example.comicstoreapp.ui.screen.seller.HomeSellerScreen
import com.example.comicstoreapp.ui.screen.seller.SellerPedidosManagementScreen
import com.example.comicstoreapp.ui.screen.user.UserComprasScreen
import com.example.comicstoreapp.ui.theme.ComicStoreAppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComicStoreAppTheme{
                AppRoot()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppRoot() {

    val navController = rememberNavController()
    val sessionVm: UserSessionViewModel = viewModel()
    val usuario = sessionVm.usuario.collectAsState().value
    val cartVm: CartViewModel = viewModel()


    val startDestination = when {
        usuario == null -> "login"
        usuario.rol.equals("admin", true) -> "admin_home"
        usuario.rol.equals("vendedor", true) -> "homeSeller"
        else -> "home"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {



        /* LOGIN */
        composable("login") {
            val loginVm: LoginViewModel = viewModel()
            LoginScreen(navController, sessionVm, loginVm)
        }
        /* REGISTER */
        composable("register") {
            val registerVm: RegisterViewModel = viewModel()
            RegisterScreen(navController, registerVm)
        }

        composable("mis_compras") { UserComprasScreen(navController, sessionVm) }
        /* HOME */
        composable("home") { HomeScreen(navController, sessionVm, cartVm) }
        /*CARRO DE COMPRAS */
        composable("carro") {
            CarroScreen(navController, sessionVm, cartVm)
        }
        /* LISTA DE CÓMICS */
        composable("comics") { backStackEntry ->
            val parent = remember(backStackEntry) { backStackEntry }
            val inventarioVm: InventarioViewModel = viewModel(parent)
            ComicScreen(navController, sessionVm, cartVm, inventarioVm)
        }
        /* PERFIL */
        composable("perfil/{id}/{nombre}/{rut}/{email}/{rol}") { backStackEntry ->
            val usuarioParam = UsuarioDto(
                id = backStackEntry.arguments!!.getString("id")!!.toLong(),
                nombre = backStackEntry.arguments!!.getString("nombre")!!,
                rut = backStackEntry.arguments!!.getString("rut")!!,
                email = backStackEntry.arguments!!.getString("email")!!,
                rol = backStackEntry.arguments!!.getString("rol")!!
            )
            EditEmailPasswordScreen(navController, sessionVm, usuarioParam)
        }


        /* DETALLE CÓMIC */
        composable("comicDetail/{id}") { backStackEntry ->
            val parentEntry = navController.getBackStackEntry("comics")
            val inventarioVm: InventarioViewModel = viewModel(parentEntry)

            val id = backStackEntry.arguments!!.getString("id")!!.toInt()
            val uiState = inventarioVm.uiState.collectAsState().value
            val comic = uiState.comics.find { it.id == id }

            if (comic != null) ComicDetailScreen(comic, cartVm)
            else Text("Comic no encontrado")
        }


        /* ADMIN */
        composable("admin_home") { AdminHomeScreen(navController, sessionVm) }
        composable("admin_add_comic") { AdminAddComicScreen(navController, sessionVm) }
        composable("admin_users") { AdminUserManagementScreen(navController, sessionVm) }
        composable("admin_products") { AdminProductManagementScreen(navController, sessionVm) }
        composable("admin_ventas") { AdminHistorialVentasScreen(navController, sessionVm) }

        composable("admin_edit_product/{id}") { backStackEntry ->
            val id = backStackEntry.arguments!!.getString("id")!!.toInt()
            AdminEditComicScreen(navController, sessionVm, id)
        }


        /* VENDEDOR*/
        composable ("homeSeller") { HomeSellerScreen(navController, sessionVm)}
        composable("seller_inventario") { SellerInventarioScreen( navController, sessionVm) }
        composable("gestionarPedidos") { SellerPedidosManagementScreen(navController, sessionVm) }





    }
}

