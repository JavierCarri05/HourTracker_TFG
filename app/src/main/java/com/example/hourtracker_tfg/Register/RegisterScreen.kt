package com.example.hourtracker_tfg.Register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.hourtracker_tfg.R

@Composable
fun RegisterScreen(){
    val context = LocalContext.current

    var gmail by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    var repeatPassword by rememberSaveable { mutableStateOf("") }
    var repeatPasswordHidden by rememberSaveable { mutableStateOf(true) }

    val showPassword = painterResource(R.drawable.show)
    val hidePassword = painterResource(R.drawable.hidee)

    fun isPasswordValid(): Boolean {
        return password.length >= 6 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Formulario de registro", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(30.dp))

        OutlinedTextField(
            value = gmail,
            onValueChange = { gmail = it},
            label = { Text("Gmail") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

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
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                when {
                    gmail.isBlank() || username.isBlank() || password.isBlank() || repeatPassword.isBlank() -> {
                        Toast.makeText(context, "Debes completar todos los campos", Toast.LENGTH_SHORT).show()
                    }
                    !gmail.endsWith("@gmail.com") -> {
                        Toast.makeText(context, "Correo invalido, porfavor introduce uno valido", Toast.LENGTH_SHORT).show()
                    }
                    password != repeatPassword -> {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    }
                    !isPasswordValid() -> {
                        Toast.makeText(context, "6 caracteres minimos, una mayuscula, un numero y un caracter especial", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(context, "Registrado con existo", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { /* Acción para volver al login */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Login")
        }
    }
}
