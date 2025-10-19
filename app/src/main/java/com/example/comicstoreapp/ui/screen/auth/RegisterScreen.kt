package com.example.comicstoreapp.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavHostController
import com.example.comicstoreapp.R
import com.example.comicstoreapp.ui.viewmodel.AuthViewModel


@Composable
fun RegisterScreenVm(navController: NavHostController, vm: AuthViewModel
){

    val state by vm.register.collectAsStateWithLifecycle()

    if (state.success){
        vm.clearRegisterResult()
        navController.navigate("login")
    }

    RegisterScreen(
        nombre = state.nombre,
        rut = state.rut,
        correo = state.correo,
        contrasena = state.contrasena,
        contrasenaConfirm = state.contrasenaConfirm,

        nombreError = state.nombreError,
        rutError = state.rutError,
        correoError = state.correoError,
        contrasenaError = state.contrasenaError,
        contrasenaConfirmError = state.contrasenaConfirmError,

        isSubmitting = state.isSubmitting,
        canSubmit = state.canSubmit,
        errorMsg = state.errorMsg,

        onNombreChange = vm::onNameChange,
        onRutChange = vm::onRutChange,
        onCorreoChange = vm::onRegisterEmailChange,
        onContrasenaChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onConfirmChange,

        onSubmit = vm::submitRegister
    )
}


@Composable
fun RegisterScreen(
    nombre: String,
    rut: String,
    correo: String,
    contrasena: String,
    contrasenaConfirm: String,

    nombreError: String?,
    rutError: String?,
    correoError: String?,
    contrasenaError: String?,
    contrasenaConfirmError: String?,

    isSubmitting: Boolean,
    canSubmit: Boolean,
    errorMsg: String?,

    onNombreChange: (String) -> Unit,
    onRutChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,

    onSubmit: () -> Unit,
) {
    val context = LocalContext.current
    var showPass by remember { mutableStateOf(false) }
    var showPassConfirm by remember { mutableStateOf(false) }



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

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp)
            )

            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = nombreError != null,
            )
            if (nombreError != null) {
                Text(text = nombreError, color = MaterialTheme.colorScheme.error
                )
            }

            // RUT
            OutlinedTextField(
                value = rut,
                onValueChange = onRutChange,
                label = { Text("RUT") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = rutError != null,
            )
            if (rutError != null) {
                Text(text = rutError, color = MaterialTheme.colorScheme.error
                )
            }


            // Email
            OutlinedTextField(
                value = correo,
                onValueChange = onCorreoChange,
                label = { Text("Correo Electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = correoError != null,
            )
            if (correoError != null) {
                Text(text = correoError, color = MaterialTheme.colorScheme.error)
            }

            // Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = onContrasenaChange,
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                isError = contrasenaError != null,
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )
            if (contrasenaError != null) {
                Text(text = contrasenaError, color = MaterialTheme.colorScheme.error)
            }

            //Confirmar Contraseña
            OutlinedTextField(
                value = contrasenaConfirm,
                onValueChange = onConfirmChange,
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                isError = contrasenaConfirmError != null,
                trailingIcon = {
                    IconButton(onClick = { showPassConfirm = !showPassConfirm }) {
                        Icon(
                            imageVector = if (showPassConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPassConfirm) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )
            if (contrasenaConfirmError != null) {
                Text(text = contrasenaConfirmError, color = MaterialTheme.colorScheme.error)
            }

            // Botón de registro
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ){
                if (isSubmitting){
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Cargando...")
                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                }else{
                    Text("Registrarse")
                }
            }

            if (errorMsg != null) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}