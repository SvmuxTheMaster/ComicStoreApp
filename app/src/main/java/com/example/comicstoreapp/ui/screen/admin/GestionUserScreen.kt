package com.example.comicstoreapp.ui.screen.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.comicstoreapp.data.local.user.UserEntity
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.admin.AdminViewModel
import com.example.comicstoreapp.ui.viewmodel.auth.AuthViewModel

@Composable
fun GestionUserScreenVm(
    navController: NavHostController,
    authVm: AuthViewModel,
    adminVm: AdminViewModel
) {
    val users by adminVm.admin.collectAsStateWithLifecycle()

    AppScaffold(
        rol = "admin",
        navController = navController,
        onLogout = {
            authVm.onLogOut()
            navController.navigate("login") { popUpTo("login") { inclusive = true } }
        }
    ) {

        GestionUserScreen(
            users = users.users,
            onDeleteUser = { user -> adminVm.deleteUser(user) },
            onChangeRole = { userId, newRole -> adminVm.cambiarRol(userId, newRole) }
        )
    }
}

@Composable
fun GestionUserScreen(
    users: List<UserEntity>,
    onDeleteUser: (UserEntity) -> Unit,
    onChangeRole: (Long, String) -> Unit
) {
    val context = LocalContext.current

    // Diálogo de confirmación
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Gestión de usuarios", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        users.forEach { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nombre: ${user.nombre}")
                    Text("Rut: ${user.rut}")
                    Text("Correo: ${user.correo}")
                    Text("Rol: ${user.rol}")

                    Spacer(modifier = Modifier.height(12.dp))

                    // Menú desplegable para cambiar rol
                    var expanded by remember { mutableStateOf(false) }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Button(onClick = { expanded = true }) {
                                Text("Cambiar Rol")
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Hacer Admin") },
                                    onClick = {
                                        expanded = false
                                        onChangeRole(user.idUsuario, "admin")
                                        Toast.makeText(context, "Rol cambiado a admin", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Hacer Vendedor") },
                                    onClick = {
                                        expanded = false
                                        onChangeRole(user.idUsuario, "vendedor")
                                        Toast.makeText(context, "Rol cambiado a vendedor", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }

                        // Botón para eliminar usuario
                        Button(
                            onClick = { userToDelete = user },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de eliminación
    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Seguro que deseas eliminar a ${userToDelete?.nombre}?") },
            confirmButton = {
                TextButton(onClick = {
                    userToDelete?.let { onDeleteUser(it) }
                    Toast.makeText(context, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
                    userToDelete = null
                }) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}