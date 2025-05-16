package com.example.hourtracker_tfg.Navegation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hourtracker_tfg.BDD.SessionManager
import com.example.hourtracker_tfg.ScreensApp.Inicio.HourTrackerScreen
import com.example.hourtracker_tfg.Login.LoginScreen
import com.example.hourtracker_tfg.Registro.registroScreen
import com.example.hourtracker_tfg.ScreensApp.Ajustes.AjustesScreen
import com.example.hourtracker_tfg.ScreensApp.Eventos.eventosScreen
import com.example.hourtracker_tfg.ScreensApp.Sumario.DetalleDia.DetalleTurnosScreen
import com.example.hourtracker_tfg.ScreensApp.Sumario.SumarioScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationScreens() {
    val navController = rememberNavController()
    var idUsuario by rememberSaveable { mutableStateOf(0) }
    val context = LocalContext.current
    val sesionManager = SessionManager(context)

    LaunchedEffect(Unit) {
        val userId = sesionManager.getUserId()
        if (userId != null) {
            // Hay sesión guardada, navegar directamente a HourTrackerScreen
            idUsuario = userId
            navController.navigate("hourTrackerScreen/$idUsuario") {
                popUpTo(0)
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") { // Ruta inicial: Login
        composable("login") {
            LoginScreen(
                navigateToRegistro = { navController.navigate("registro") }, // Navegar al registro
                navigateToInicio = { id ->
                    idUsuario = id // Guardar el id del usuario
                    navController.navigate("hourTrackerScreen/$idUsuario") // Navegar a la pantalla principal con el ID
                }
            )
        }

        composable("registro") {
            registroScreen(
                navigateToLogin = { navController.navigate("login") }, // Navegar a login
                navigateToInicio = { id ->
                    idUsuario = id // Guardar el id del usuario
                    navController.navigate("hourTrackerScreen/$idUsuario") // Navegar a la pantalla principal con el ID
                }
            )
        }

        // Aquí definimos la ruta dinámica para HourTrackerScreen
        composable("hourTrackerScreen/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
            HourTrackerScreen(
                idUsuario = id,
                navController = navController
            )
        }


        //Navegacion hacia el sumario Screen
        composable("sumarioScreen/{idUsuario}"){ backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toInt() ?: 0
            SumarioScreen(idUsuario = idUsuario, navController = navController)
        }

        //Navegacion para el detalleTurnosScreen
        composable("detalleTurnosScreen/{idUsuario}/{fecha}") { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toInt() ?: 0
            val fecha = backStackEntry.arguments?.getString("fecha")?.replace("-", "/") ?: ""
            DetalleTurnosScreen(
                idUsuario = idUsuario,
                fecha = fecha,
                navController = navController
            )
        }

        //navegacion a eventosScreen
        composable("eventosScreen/{idUsuario}"){ backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toInt() ?: 0
            eventosScreen(idUsuario = idUsuario, navController = navController)
        }

        /*
        Navegacion a los ajustes
        y si pulso el boton de cerra sesion me envia al loginScreen
         */
        composable("ajustesScreen/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
            AjustesScreen(
                idUsuario = id,
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo("hourTrackerScreen/{id}") { inclusive = true }
                    }
                },
                navController = navController
            )
        }

    }
}