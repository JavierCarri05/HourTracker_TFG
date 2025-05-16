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
import kotlinx.coroutines.selects.select

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
        // Eventos
        NavigationBarItem(
            selected = selectedItem == "Eventos",
            onClick = {
                if (selectedItem != "Eventos") {
                    navController.navigate("eventosScreen/$idUsuario")
                }
            },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Eventos") },
            label = { Text("Eventos") }
        )
        // Ajustes
        NavigationBarItem(
            selected = selectedItem == "Ajustes",
            onClick = {
                if(selectedItem != "Ajustes" ){
                    navController.navigate("ajustesScreen/$idUsuario")
                }
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
            label = { Text("Ajustes") }
        )
    }
}

