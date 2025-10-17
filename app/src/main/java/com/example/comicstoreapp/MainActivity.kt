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
import com.example.comicstoreapp.ui.screen.admin.AdminScreen
import com.example.comicstoreapp.ui.screen.auth.LoginScreenVm
import com.example.comicstoreapp.ui.screen.auth.RegisterScreenVm
import com.example.comicstoreapp.ui.screen.user.DCScreen
import com.example.comicstoreapp.ui.screen.user.HomeScreen
import com.example.comicstoreapp.ui.screen.user.MangaScreen
import com.example.comicstoreapp.ui.screen.user.MarvelScreen
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
        composable("login") { LoginScreenVm(navController, authViewModel) }
        composable("register") { RegisterScreenVm(navController, authViewModel) }
        composable("home") { HomeScreen(navController) }
        composable("dc_comics") { DCScreen(navController) }
        composable("marvel_comics") { MarvelScreen(navController) }
        composable("manga") { MangaScreen(navController) }
        composable("homeAdmin") { AdminScreen(navController) }
        composable("homeSeller") { SellerScreen(navController) }
    }
}
