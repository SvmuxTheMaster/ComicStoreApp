package com.example.comicstoreapp.ui.screen.user

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioDto
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.auth.EditEmailPassViewModel
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmailPasswordScreen(
    navController: NavHostController,
    sessionVm: UserSessionViewModel,
    usuario: UsuarioDto,
    vm: EditEmailPassViewModel = viewModel()
) {
    val ui = vm.uiState.collectAsState().value
    val ctx = LocalContext.current

    // Inicializar email SOLO una vez
    LaunchedEffect(Unit) {
        vm.initData(usuario.email)
    }

    // Cuando success = true → volver atrás
    LaunchedEffect(ui.success) {
        if (ui.success) {
            Toast.makeText(ctx, "Datos actualizados", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    AppScaffold(
        sessionVm = sessionVm,
        navController = navController,
        onLogout = {
            sessionVm.cerrarSesion()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 28.dp)
        ) {

            Text(
                "Editar información",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Actualiza tu correo y contraseña",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // ---------- CARD CONTENEDOR ----------
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    // ---------- EMAIL ----------
                    Column {
                        Text("Correo electrónico", fontWeight = FontWeight.SemiBold)

                        Spacer(Modifier.height(6.dp))

                        OutlinedTextField(
                            value = ui.email,
                            onValueChange = vm::onEmailChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            isError = ui.emailError != null,
                            supportingText = {
                                ui.emailError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }

                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                    // ---------- PASSWORD ----------
                    Column {
                        Text("Nueva contraseña", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))

                        var passwordVisible by remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = ui.password,
                            onValueChange = vm::onPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            isError = ui.passwordError != null,
                            visualTransformation =
                                if (passwordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Default.VisibilityOff
                                        else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            supportingText = {
                                ui.passwordError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }

                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                    // ---------- CONFIRMAR ----------
                    Column {
                        Text("Confirmar contraseña", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))

                        var confirmVisible by remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = ui.confirmPassword,
                            onValueChange = vm::onConfirmChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            isError = ui.confirmError != null,
                            visualTransformation =
                                if (confirmVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                    Icon(
                                        if (confirmVisible) Icons.Default.VisibilityOff
                                        else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            supportingText = {
                                ui.confirmError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                }
            }

            // ---------- ERROR DEL SERVIDOR ----------
            ui.error?.let {
                Spacer(Modifier.height(12.dp))
                Text("⚠ $it", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            // ---------- BOTÓN GUARDAR ----------
            Button(
                onClick = { vm.actualizar(usuario.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !ui.loading
            ) {
                Text(
                    if (ui.loading) "Guardando..." else "Guardar cambios",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
