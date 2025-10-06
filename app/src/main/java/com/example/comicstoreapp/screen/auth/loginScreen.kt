package com.example.comicstoreapp.screen.auth


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.comicstoreapp.R
import androidx.navigation.NavHostController


@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize(),
            //.background(Color(red = 63, green = 64, blue = 128)),
            contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {

            Logo()

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electronico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),

            )

            Button(
                onClick = {
                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    navController.navigate("home"){
                        popUpTo("login"){ inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }


            Button(
                onClick = {
                    navController.navigate("register"){
                        popUpTo("login"){ inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿No tienes cuenta?")

            }
        }
    }
}


@Composable
fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.logo), // mi imagen
        modifier = Modifier.size(250.dp), // tamaño de mi imagen
        contentDescription = "Logo"

    )
}