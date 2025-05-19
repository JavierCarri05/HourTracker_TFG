package com.example.hourtracker_tfg.app.Screens.ajustes

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hourtracker_tfg.app.core.SessionManager
import com.example.hourtracker_tfg.BDD.TurnosDataBaseHelper
import com.example.hourtracker_tfg.app.data.helpers.LoginDateBaseHelper
import com.example.hourtracker_tfg.R
import com.example.hourtracker_tfg.app.Screens.components.BarraNavegacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    idUsuario: Int,
    navController: NavController,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = TurnosDataBaseHelper(context)
    val sessionManager = SessionManager(context)
    val loginDBH = LoginDateBaseHelper(context)

    var showResetDialog by remember { mutableStateOf(false) }
    var usernameForPasswordReset by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var usernameForPasswordResetError by remember { mutableStateOf(false) }
    var newPasswordHidden by remember { mutableStateOf(true) }
    var repeatPasswordHidden by remember { mutableStateOf(true) }
    val showPassword = painterResource(R.drawable.show)
    val hidePassword = painterResource(R.drawable.hidee)

    Scaffold(
        bottomBar = {
            BarraNavegacion(
                selectedItem = "Ajustes",
                idUsuario = idUsuario,
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ajustes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    sessionManager.cerrarSesion()
                    Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    onCerrarSesion()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F7)
                )
            ) {
                Text("Cerrar Sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F7)
                )
            ) {
                Text("Restablecer Contraseña")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    dbHelper.borrarDatos(idUsuario)
                    Toast.makeText(context, "Datos borrados correctamente", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F7)
                )
            ) {
                Text("Borrar Datos")
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (usernameForPasswordReset.isNotBlank() && newPassword == repeatPassword) {
                                val ok = loginDBH.cambiarContrasena(usernameForPasswordReset, newPassword)
                                if (ok) {
                                    Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                                    showResetDialog = false
                                    usernameForPasswordResetError = false
                                } else {
                                    usernameForPasswordResetError = true
                                    Toast.makeText(context, "Nombre de usuario incorrecto", Toast.LENGTH_SHORT).show()
                                }
                            } else if (newPassword != repeatPassword) {
                                Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                            } else {
                                usernameForPasswordResetError = true
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF3B82F7)
                        )
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showResetDialog = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF3B82F7)
                        )
                    ) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Restablecer contraseña") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = usernameForPasswordReset,
                            onValueChange = { usernameForPasswordReset = it },
                            label = { Text("Nombre de usuario") },
                            isError = usernameForPasswordResetError,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3B82F7),
                                unfocusedBorderColor = Color(0xFF3B82F7),
                                focusedLabelColor = Color(0xFF3B82F7),
                                cursorColor = Color(0xFF3B82F7)
                            )
                        )
                        if (usernameForPasswordResetError) {
                            Text(
                                text = "Este usuario no existe.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Nueva Contraseña") },
                            visualTransformation = if (newPasswordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { newPasswordHidden = !newPasswordHidden }) {
                                    Icon(
                                        painter = if (newPasswordHidden) showPassword else hidePassword,
                                        contentDescription = if (newPasswordHidden) "Mostrar contraseña" else "Ocultar contraseña"
                                    )
                                }
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3B82F7),
                                unfocusedBorderColor = Color(0xFF3B82F7),
                                focusedLabelColor = Color(0xFF3B82F7),
                                cursorColor = Color(0xFF3B82F7)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = repeatPassword,
                            onValueChange = { repeatPassword = it },
                            label = { Text("Repetir contraseña") },
                            visualTransformation = if (repeatPasswordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { repeatPasswordHidden = !repeatPasswordHidden }) {
                                    Icon(
                                        painter = if (repeatPasswordHidden) showPassword else hidePassword,
                                        contentDescription = if (repeatPasswordHidden) "Mostrar contraseña" else "Ocultar contraseña"
                                    )
                                }
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3B82F7),
                                unfocusedBorderColor = Color(0xFF3B82F7),
                                focusedLabelColor = Color(0xFF3B82F7),
                                cursorColor = Color(0xFF3B82F7)
                            )
                        )
                    }
                }
            )
        }
    }
}