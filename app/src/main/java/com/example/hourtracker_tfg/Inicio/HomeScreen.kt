package com.example.hourtracker_tfg.Inicio

/**
 * Este fichero es de ejemplo para hacer pruebas con el
 * login y el register
 */

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido a la Pantalla Principal", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { /* Acción adicional o navegar a otra pantalla */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Realizar Acción")
        }
    }
}