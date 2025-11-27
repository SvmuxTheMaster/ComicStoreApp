package com.example.comicstoreapp.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.comicstoreapp.data.remote.dto.usuario.UsuarioDto
import com.example.comicstoreapp.ui.components.AppScaffold
import com.example.comicstoreapp.ui.viewmodel.admin.AdminUsersViewModel
import com.example.comicstoreapp.ui.viewmodel.auth.UserSessionViewModel
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comicstoreapp.data.repository.UsuarioAdminRepository
import com.example.comicstoreapp.ui.viewmodel.admin.AdminUsersViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserManagementScreen(
    navController: NavHostController,
    sessionVm: UserSessionViewModel = viewModel()
) {
    val repo = remember { UsuarioAdminRepository() }
    val factory = remember { AdminUsersViewModelFactory(sessionVm, repo) }

    val vm: AdminUsersViewModel = viewModel(factory = factory)
    val state by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.cargarUsuarios() }

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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            Text(
                "Gestión de Usuarios",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            if (state.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.usuarios) { usuario ->
                    UserItemCard(
                        usuario = usuario,
                        onRoleChange = { id, rol -> vm.cambiarRol(id, rol) },
                        onDelete = { id -> vm.eliminarUsuario(id) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserItemCard(
    usuario: UsuarioDto,
    onRoleChange: (Long, String) -> Unit,
    onDelete: (Long) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    // Color dinámico según rol
    val roleColor = when (usuario.rol.lowercase()) {
        "admin" -> MaterialTheme.colorScheme.primaryContainer
        "vendedor" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }

    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                // --- INFO DEL USUARIO ---
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        usuario.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        usuario.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(10.dp))

                    // --- CHIP DE ROL ---
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(roleColor)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            usuario.rol.uppercase(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // --- MENÚ ---
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "menu")
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Hacer Admin") },
                            onClick = {
                                menuExpanded = false
                                onRoleChange(usuario.id, "admin")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hacer Vendedor") },
                            onClick = {
                                menuExpanded = false
                                onRoleChange(usuario.id, "vendedor")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hacer Usuario") },
                            onClick = {
                                menuExpanded = false
                                onRoleChange(usuario.id, "usuario")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menuExpanded = false
                                onDelete(usuario.id)
                            }
                        )
                    }
                }
            }
        }
    }
}