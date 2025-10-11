package com.example.comicstoreapp

import androidx.navigation.compose.NavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.comicstoreapp.ui.screen.auth.LoginScreenVm
import com.example.comicstoreapp.ui.screen.auth.RegisterScreen
import com.example.comicstoreapp.ui.screen.user.DCScreen
import com.example.comicstoreapp.ui.screen.user.HomeScreen
import com.example.comicstoreapp.ui.screen.user.MangaScreen
import com.example.comicstoreapp.ui.screen.user.MarvelScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            Surface {
                NavHost(
                    navController = navController,
                    startDestination = "login"
                )
                {
                    composable("login") { LoginScreenVm(navController) }
                    composable("register") { RegisterScreen(navController) }

                    composable("home") { HomeScreen(navController) }
                    composable("dc_comics") { DCScreen(navController) }
                    composable("marvel_comics") { MarvelScreen(navController) }
                    composable("manga") { MangaScreen(navController) }

                }
            }
        }
    }
}
