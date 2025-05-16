package com.example.hourtracker_tfg.Registro

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
import com.example.hourtracker_tfg.BDD.SessionManager
import com.example.hourtracker_tfg.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun registroScreen(navigateToLogin: () -> Unit, navigateToInicio: (Int) -> Unit){
    val context = LocalContext.current
    val registrarDBH = RegistroDateBaseHelper(context) //Llamo al DateBaseHelper de register
    val sessionManager = SessionManager(context) // para guardar la sesion

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
    fun validarEmail(): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")
        return gmail.matches(emailRegex)
    }
    //funcion para validar el nombre de usuario o el correo ya existen y tambien que las contraseña coinciden
    fun validarUsuario(){
        gmailError = !validarEmail() || registrarDBH.existeGmail(gmail)
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
        Text("Formulario de registro",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(30.dp))

        OutlinedTextField(
            value = gmail,
            onValueChange = { gmail = it},
            label = { Text("Email") },
            isError =  gmailError,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF3B82F7), // borde azul cuando enfocado
                unfocusedBorderColor = Color(0xFF3B82F7), // borde azul cuando NO esta enfocado
                focusedLabelColor = Color(0xFF3B82F7), // label azul cuando enfocado
                cursorColor = Color(0xFF3B82F7) // cursor azul al escribir
            )
        )

        if(gmailError){
            Text(
                text = "Este correo ya esta registrado o no es valido",
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
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF3B82F7), // borde azul cuando enfocado
                unfocusedBorderColor = Color(0xFF3B82F7), // borde azul cuando NO esta enfocado
                focusedLabelColor = Color(0xFF3B82F7), // label azul cuando enfocado
                cursorColor = Color(0xFF3B82F7) // cursor azul al escribir
            )
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
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF3B82F7), // borde azul cuando enfocado
                unfocusedBorderColor = Color(0xFF3B82F7), // borde azul cuando NO esta enfocado
                focusedLabelColor = Color(0xFF3B82F7), // label azul cuando enfocado
                cursorColor = Color(0xFF3B82F7) // cursor azul al escribir
            )
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
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF3B82F7), // borde azul cuando enfocado
                unfocusedBorderColor = Color(0xFF3B82F7), // borde azul cuando NO esta enfocado
                focusedLabelColor = Color(0xFF3B82F7), // label azul cuando enfocado
                cursorColor = Color(0xFF3B82F7) // cursor azul al escribir
            )
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
                    val idUsuario = registrarDBH.obtenerIdUsuario(nombreUsuario)//Obtengo el id del usuario para trabajar sobre ese usuario
                    sessionManager.guardarIdUsuario(idUsuario) //Guardo la sesion
                    Toast.makeText(context, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                    navigateToInicio(idUsuario)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = gmail.isNotBlank() && nombreUsuario.isNotBlank() && contrasena.isNotBlank() && repetirContrasena.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F7)
            )
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navigateToLogin() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F7)
            )
        ) {
            Text("Iniciar Sesión")
        }
    }
}
