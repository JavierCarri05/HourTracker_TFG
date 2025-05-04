package com.example.hourtracker_tfg.ScreensApp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.hourtracker_tfg.R

/**
 * Este Composable es el menú inferior reutilizable.
 * Se usa en TODAS las pantallas que tienen el menú (Inicio, Sumario, Gestionar, Ajustes).
 */
@Composable
fun BarraNavegacion(
    selectedItem: String,
    idUsuario: Int,
    navController: NavController
) {
    val icon = painterResource(id = R.drawable.time)

    NavigationBar {
        // Inicio
        NavigationBarItem(
            selected = selectedItem == "Inicio",
            onClick = {
                if (selectedItem != "Inicio") {
                    navController.navigate("hourTrackerScreen/$idUsuario")
                }
            },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        // Sumario
        NavigationBarItem(
            selected = selectedItem == "Sumario",
            onClick = {
                if (selectedItem != "Sumario") {
                    navController.navigate("sumarioScreen/$idUsuario")
                }
            },
            icon = { Icon(painter = icon, contentDescription = "Sumario") },
            label = { Text("Sumario") }
        )
        // Gestionar
        NavigationBarItem(
            selected = selectedItem == "Gestionar",
            onClick = {
                if (selectedItem != "Gestionar") {
                    navController.navigate("gestionarScreen/$idUsuario")
                }
            },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Gestionar") },
            label = { Text("Gestionar") }
        )
        // Ajustes
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("ajustesScreen/$idUsuario") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
            label = { Text("Ajustes") }
        )
    }
}

