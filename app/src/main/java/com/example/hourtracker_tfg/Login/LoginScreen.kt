package com.example.hourtracker_tfg.Login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.hourtracker_tfg.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navigateToRegister: () -> Unit, navigateToHomeHourTracker: (Int) -> Unit) {
    val context = LocalContext.current
    val loginDBH = LoginDateBaseHelper(context)

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    val showPassword = painterResource(R.drawable.show)
    val hidePassword = painterResource(R.drawable.hidee)
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    // Variables para cambiar la contraseña en el dialog
    var dialog by remember { mutableStateOf(false) }
    var usernameForPasswordReset by rememberSaveable { mutableStateOf("") } //Pido el nombre del usuario para restablecer la contraseña
    var newPassword by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf("") }
    var usernameForPasswordResetError by rememberSaveable { mutableStateOf(false) }
    var newPasswordHidden by rememberSaveable { mutableStateOf(true) }
    var repeatPasswordHidden by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Iniciar Sesión",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(30.dp))

        // Input del usuario
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF3B82F7), // borde azul cuando enfocado
                unfocusedBorderColor = Color(0xFF3B82F7), // borde azul cuando NO esta enfocado
                focusedLabelColor = Color(0xFF3B82F7), // label azul cuando enfocado
                cursorColor = Color(0xFF3B82F7) // cursor azul al escribir
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input de la contraseña
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
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF3B82F7), // borde azul cuando enfocado
                unfocusedBorderColor = Color(0xFF3B82F7), // borde azul cuando NO esta enfocado
                focusedLabelColor = Color(0xFF3B82F7), // label azul cuando enfocado
                cursorColor = Color(0xFF3B82F7) // cursor azul al escribir
            )
        )

        // Olvidar la contraseña
        TextButton(
            onClick = { dialog = true },
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFF3B82F7)
            )
        ) {
            Text(text = "¿Olvidaste tu contraseña?")
        }


        // Mensaje de error por si algo falla, como puede ser que la contraseña o el nombre de usuario no sean correctos
        if (errorMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Boton de iniciar sesion
        Button(
            onClick = {
                // Validar las credenciales del usuario
                if (loginDBH.comprobarUsuario(username, password)) {
                    val idUsuario = loginDBH.obtenerIdUsuario(username)
                    Toast.makeText(context, "Iniciando sesion...", Toast.LENGTH_SHORT).show()
                    navigateToHomeHourTracker(idUsuario) //Nos envia a la homeScreen, pero con el id del usuario para poder trabajar sobre ese usuario
                } else {
                    errorMessage = "Usuario o contraseña incorrectos"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = username.isNotBlank() && password.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F7)
            )
        ) {
            Text("Iniciar Sesión")
        }

        // Restablecer contraseña Dialog
        if (dialog) {
            AlertDialog(
                onDismissRequest = { dialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        // Compruebo si el nombre del usuario es correcto y la nueva contraseña coincide
                        if (usernameForPasswordReset.isNotBlank() && newPassword == repeatPassword) {
                            //Llamo al metodo para cambiar la contraseña
                            val TodoOk = loginDBH.cambiarContrasena(usernameForPasswordReset, newPassword)
                            if (TodoOk) {
                                Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                                dialog = false
                            } else {
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
                    TextButton(onClick = { dialog = false },
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
                        // Ingresas el nombre del usuario para restablecer la contraseña
                        OutlinedTextField(
                            value = usernameForPasswordReset,
                            onValueChange = { usernameForPasswordReset = it },
                            label = { Text("Nombre de usuario") },
                            isError = usernameForPasswordResetError,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3B82F7), // borde azul cuando enfocado
                                unfocusedBorderColor = Color(0xFF3B82F7), // borde azul cuando NO esta enfocado
                                focusedLabelColor = Color(0xFF3B82F7), // label azul cuando enfocado
                                cursorColor = Color(0xFF3B82F7) // cursor azul al escribir
                            )
                        )
                        if (usernameForPasswordResetError) { //Si el usuario no existe, salta un mensaje de que no existe
                            Text(
                                text = "Este usuario no existe.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Ingresar la nueva contraseña
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
                                focusedBorderColor = Color(0xFF3B82F7), // borde azul cuando enfocado
                                unfocusedBorderColor = Color(0xFF3B82F7), // borde azul cuando NO esta enfocado
                                focusedLabelColor = Color(0xFF3B82F7), // label azul cuando enfocado
                                cursorColor = Color(0xFF3B82F7) // cursor azul al escribir
                            )
                        )
                        // Repetir la nueva contraseña
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
                                focusedBorderColor = Color(0xFF3B82F7), // borde azul cuando enfocado
                                unfocusedBorderColor = Color(0xFF3B82F7), // borde azul cuando NO esta enfocado
                                focusedLabelColor = Color(0xFF3B82F7), // label azul cuando enfocado
                                cursorColor = Color(0xFF3B82F7) // cursor azul al escribir
                            )
                        )
                    }
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        // Ir al formulario de registro
        Button(
            onClick = { navigateToRegister() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F7)
            )
        ) {
            Text("Registrar")
        }

    }
}
