package com.example.hourtracker_tfg.Login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.hourtracker_tfg.R

@Composable
fun LoginScreen() {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val showPassword = painterResource(R.drawable.show)
    val hidePassword = painterResource(R.drawable.hidee)
    var passwordHidden by rememberSaveable { mutableStateOf(true) }


    var newPassword by rememberSaveable { mutableStateOf("") }
    val showNewPassword = painterResource(R.drawable.show)
    val hideNewPassword = painterResource(R.drawable.hidee)
    var newPasswordHidden by rememberSaveable { mutableStateOf(true) }


    var repeatPassword by rememberSaveable { mutableStateOf("") }
    val showRepeatPassword = painterResource(R.drawable.show)
    val hideRepatPassword = painterResource(R.drawable.hidee)
    var repeatPasswordHidden by rememberSaveable { mutableStateOf(true) }

    var dialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(30.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    Icon(
                        painter = if (passwordHidden) showPassword else hidePassword,
                        contentDescription = if (passwordHidden) "Mostrar contraseña" else "Ocultar contraseña"
                    )
                }
            }
        )

        TextButton(onClick = { dialog = true }) {
            Text("¿Olvidaste tu contraseña?")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* Iniciar Sesion */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = username.isNotBlank() && password.isNotBlank()
        ) {
            Text("Login")
        }

        if (dialog) {
            AlertDialog(
                onDismissRequest = { dialog = false },
                confirmButton = {
                    TextButton(onClick = { /* cambiar contraseña */ dialog = false }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { dialog = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Restablecer la contraseña") },
                text = {
                    Column {
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
                                        painter = if (newPasswordHidden) showNewPassword else hideNewPassword,
                                        contentDescription = if (newPasswordHidden) "Mostrar contraseña" else "Ocultar contraseña"
                                    )
                                }
                            }
                        )
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
                                        painter = if (repeatPasswordHidden) showRepeatPassword else hideRepatPassword,
                                        contentDescription = if (repeatPasswordHidden) "Mostrar contraseña" else "Ocultar contraseña"
                                    )
                                }
                            }
                        )
                    }
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { /* Registrar */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }
    }
}
