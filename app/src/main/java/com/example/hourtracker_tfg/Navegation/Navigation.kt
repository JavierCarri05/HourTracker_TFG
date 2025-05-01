package com.example.hourtracker_tfg.Navegation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hourtracker_tfg.ScreensApp.HourTrackerScreen
import com.example.hourtracker_tfg.Login.LoginScreen
import com.example.hourtracker_tfg.Register.RegisterScreen

@Composable
fun NavigationScreens() {
    val navController = rememberNavController()
    var idUsuario by rememberSaveable { mutableStateOf(0) }

    NavHost(navController = navController, startDestination = "login") { // Ruta inicial: Login
        composable("login") {
            LoginScreen(
                navigateToRegister = { navController.navigate("register") }, // Navegar a register
                navigateToHomeHourTracker = { id ->
                    idUsuario = id // Guardar el id del usuario
                    navController.navigate("hourTrackerScreen/$idUsuario") // Navegar a la pantalla principal con el ID
                }
            )
        }

        composable("register") {
            RegisterScreen(
                navigateToLogin = { navController.navigate("login") }, // Navegar a login
                navigateToHomeHourTracker = { id ->
                    idUsuario = id // Guardar el id del usuario
                    navController.navigate("hourTrackerScreen/$idUsuario") // Navegar a la pantalla principal con el ID
                }
            )
        }

        // Aquí definimos la ruta dinámica para HourTrackerScreen
        composable("hourTrackerScreen/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0 // Obtener el ID de la ruta
            HourTrackerScreen(idUsuario = id) // Pasar el ID del usuario al composable
        }
    }
}
