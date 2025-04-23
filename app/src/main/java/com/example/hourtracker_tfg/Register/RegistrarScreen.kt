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
fun RegisterScreen(navigateToLogin: () -> Unit, navigateToHome: () -> Unit){
    val context = LocalContext.current
    val registrarDBH = RegistrarDateBaseHelper(context) //Llamo al DateBaseHelper de register

    var gmail by rememberSaveable { mutableStateOf("") }
    var gmailError  by rememberSaveable { mutableStateOf(false) }

    var nombreUsuario by rememberSaveable { mutableStateOf("") }
    var nombreUsuarioError  by rememberSaveable { mutableStateOf(false) }

    var contrasena by rememberSaveable { mutableStateOf("") }//variable de la contrasela
    var contrasenaError  by rememberSaveable { mutableStateOf(false) } //Variable para comprobar que las contraseña coincidan
    var repetirContrasena by rememberSaveable { mutableStateOf("") }//Repetir contraseña
    var repetirContrasenaError by rememberSaveable { mutableStateOf(false) }//variable para repetir contraseña

    var mostrarContrasena by rememberSaveable { mutableStateOf(true) }//Ver cotnraseña
    var mostrarRepetirContrasena by rememberSaveable { mutableStateOf(true) }//ver repetir contraseña
    val icMostrar = painterResource(R.drawable.show)//Mostrar
    val icOcultar = painterResource(R.drawable.hidee)//Ocultar

    /*  Funcion para que la contraseña tenga:
    *   - Minimo 6 digitos
    *   - 1 mayuscula
    *   - 1 numero
    *   - 1 caracter especial
    */
    fun validarContrasena(): Boolean {
        return contrasena.length >= 6 &&
                contrasena.any { it.isUpperCase() } &&
                contrasena.any { it.isDigit() } &&
                contrasena.any { !it.isLetterOrDigit() }
    }

    //funcion para validar que el gmail acabe en @gmail.com
    fun validarGmail(): Boolean{
        return gmail.endsWith("@gmail.com")
    }

    //funcion para validar el nombre de usuario o el correo ya existen y tambien que las contraseña coinciden
    fun validarUsuario(){
        gmailError = !validarGmail() || registrarDBH.existeGmail(gmail)
        nombreUsuarioError = registrarDBH.existeNombreUsuario(nombreUsuario)
        contrasenaError = contrasena != repetirContrasena
        contrasenaError = !validarContrasena()
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
            isError =  gmailError,
            modifier = Modifier.fillMaxWidth()
        )

        if(gmailError){
            Text(
                text = "Este correo ya esta registrado o no es valido (debe terminar en @gmail.com)",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            label = { Text("Usuario") },
            isError = nombreUsuarioError,
            modifier = Modifier.fillMaxWidth()
        )
        if(nombreUsuarioError){
            Text(
                text = "Este nombre de usuario ya existe",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = if (mostrarContrasena) PasswordVisualTransformation() else VisualTransformation.None,
            isError = contrasenaError,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                    Icon(
                        painter = if (mostrarContrasena) icMostrar else icOcultar,
                        contentDescription = if (mostrarContrasena) "Mostrar contraseña" else "Ocultar contraseña"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = repetirContrasena,
            onValueChange = { repetirContrasena = it },
            label = { Text("Repetir contraseña") },
            visualTransformation = if (mostrarRepetirContrasena) PasswordVisualTransformation() else VisualTransformation.None,
            isError = repetirContrasenaError,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { mostrarRepetirContrasena = !mostrarRepetirContrasena }) {
                    Icon(
                        painter = if (mostrarRepetirContrasena) icMostrar else icOcultar,
                        contentDescription = if (mostrarRepetirContrasena) "Mostrar contraseña" else "Ocultar contraseña"
                    )
                }
            }
        )
        if(repetirContrasenaError){
            Text(
                text = "Las contraseña no coinciden",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                validarUsuario()
                if(!gmailError && !nombreUsuarioError && !repetirContrasenaError && !contrasenaError){
                    //Si todos los campos son correctos, registramos al usuario
                    registrarDBH.nuevoUsuario(gmail, nombreUsuario, contrasena)
                    Toast.makeText(context, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = gmail.isNotBlank() && nombreUsuario.isNotBlank() && contrasena.isNotBlank() && repetirContrasena.isNotBlank()
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(8.dp))

       Button(
            onClick = { navigateToLogin()},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }
    }
}
