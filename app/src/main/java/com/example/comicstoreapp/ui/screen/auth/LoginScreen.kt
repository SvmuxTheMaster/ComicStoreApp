package com.example.comicstoreapp.ui.screen.auth


import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.comicstoreapp.ui.viewmodel.auth.LoginViewModel
import com.example.comicstoreapp.R
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel


@Composable
fun LoginScreen(
    navController: NavHostController,
    sessionVm: UserSessionViewModel,
    loginVm: LoginViewModel
) {
    val ui = loginVm.uiState.collectAsState().value
    val usuario = loginVm.usuario.collectAsState().value

    // --- Redirección si login OK ---
    LaunchedEffect(usuario) {
        usuario?.let {

            // Guardar sesión en DataStore
            sessionVm.guardarSesion(
                id = it.id,
                nombre = it.nombre,
                email = it.email,
                rut = it.rut,
                rol = it.rol
            )

            // Redirección según rol
            when (it.rol.lowercase()) {
                "admin" -> navController.navigate("admin_home") {
                    popUpTo("login") { inclusive = true }
                }
                "vendedor" -> navController.navigate("homeSeller") {
                    popUpTo("login") { inclusive = true }
                }
                "usuario" -> navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }

    // --- UI PRINCIPAL ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Logo()

                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(20.dp))

                // ---------- Email ----------
                OutlinedTextField(
                    value = ui.email,
                    onValueChange = loginVm::onEmailChange,
                    label = { Text("Correo") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // ---------- Password ----------
                var passVisible by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = ui.password,
                    onValueChange = loginVm::onPasswordChange,
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passVisible = !passVisible }) {
                            Icon(
                                imageVector = if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation =
                        if (passVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                // ---------- Error ----------
                ui.error?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(20.dp))

                // ---------- Botón de Ingreso ----------
                Button(
                    onClick = { loginVm.login() },
                    enabled = !ui.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (ui.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Validando...")
                    } else {
                        Text("Ingresar")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ---------- Registro ----------
                TextButton(onClick = { navController.navigate("register") }) {
                    Text("¿No tienes cuenta? Regístrate")
                }
            }
        }
    }
}



@Composable
fun Logo() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        )
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo",
        modifier = Modifier
            .size(200.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha
            )
    )
}
