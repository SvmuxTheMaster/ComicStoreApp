package com.example.comicstoreapp.ui.screen.auth


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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comicstoreapp.R
import androidx.navigation.NavHostController
import com.example.comicstoreapp.ui.viewmodel.AuthViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff



@Composable
fun LoginScreenVm(navController: NavHostController){

    val vm: AuthViewModel = viewModel()
    val state by vm.login.collectAsStateWithLifecycle()

    if (state.success){
        vm.clearLoginResult()
        navController.navigate("home")
    }

    LoginScreen(
        correo = state.correo,
        contrasena = state.contrasena,

        correoError = state.correoError,
        contrasenaError = state.contrasenaError,

        isSubmitting = state.isSubmitting,
        canSubmit = state.canSubmit,
        errorMsg = state.errorMsg,

        onCorreoChange = vm::onLoginEmailChange,
        onContrasenaChange = vm::onLoginPassChange,
        onSubmit = vm::submitLogin,
        onGoRegister = { navController.navigate("register") }
    )
}


@Composable
public fun LoginScreen(
    correo: String,
    contrasena: String,

    correoError: String?,
    contrasenaError: String?,

    isSubmitting: Boolean,
    canSubmit: Boolean,
    errorMsg: String?,

    onCorreoChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoRegister: () -> Unit,

) {
    val context = LocalContext.current
    var showPass by remember { mutableStateOf(false) }


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
                value = correo,
                onValueChange = onCorreoChange,
                label = { Text("Correo Electronico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
            )
            if (correoError != null) {
                Text(
                    text = correoError,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = contrasena,                                // Valor actual
                onValueChange = onContrasenaChange,                // Notifica VM
                label = { Text("Contraseña") },              // Etiqueta
                singleLine = true,                           // Una línea
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(), // Toggle mostrar/ocultar
                trailingIcon = {                             // Ícono para alternar visibilidad
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                isError = contrasenaError != null,                 // (Opcional) marcar error
                modifier = Modifier.fillMaxWidth()           // Ancho completo
            )
            if (contrasenaError != null) {                         // (Opcional) mostrar error
                Text(contrasenaError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isSubmitting){
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Cargando...")
                }else{
                    Text("Iniciar Sesion")
                }
            }

            if (errorMsg != null) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }



            Button(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()){
                Text("Registrarse")
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