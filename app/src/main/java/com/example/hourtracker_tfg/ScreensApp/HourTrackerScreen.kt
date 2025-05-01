package com.example.hourtracker_tfg.ScreensApp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hourtracker_tfg.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HourTrackerScreen(idUsuario: Int) {
    val icon = painterResource(id = R.drawable.time)
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var currentDay by remember { mutableStateOf(getCurrentDay()) }
    var currentDayName by remember { mutableStateOf(getDayName()) }
    var currentMonth by remember { mutableStateOf(getCurrentMonth()) }
    var currentWeek by remember { mutableStateOf(getCurrentWeek()) }
    /**Esto esto es para el BottomSheetScaffold*/
    var showBottomSheet by remember { mutableStateOf(false) }

    // Método para actualizar la hora consecutivamente
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = getCurrentTime()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(painter = icon, contentDescription = "Sumario") },
                    label = { Text("Sumario") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Gestionar") },
                    label = { Text("Gestionar") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                    label = { Text("Ajustes") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "HourTracker",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = currentTime,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = currentDay,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showBottomSheet = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nueva Actividad")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Utilizamos un Box con peso para ayudar a distribuir el espacio
            Box(modifier = Modifier.weight(1f, fill = false)) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Hoy - con solo el nombre del día
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hoy",
                                fontWeight = FontWeight.Medium
                            )
                            Text(text = currentDayName)
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "0h 00m")
                                Text(text = "00,00 €")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Semana
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Semana",
                                fontWeight = FontWeight.Medium
                            )
                            Text(text = "nº $currentWeek")
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "0h 00m")
                                Text(text = "00,00 €")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mes",
                                fontWeight = FontWeight.Medium
                            )
                            Text(text = currentMonth)
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "0h 00m")
                                Text(text = "00,00 €")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }

        //Muestro el BottomSheet si esta activo
        if (showBottomSheet) {
            BottomShet(onDismiss = { showBottomSheet = false })
        }

    }
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale("es", "ES"))  // Establecer Locale a español
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")  // Configurar la zona horaria a España
    return sdf.format(Date())
}

fun getCurrentDay(): String {
    val sdf = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "ES"))  // Establecer Locale a español
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")  // Configurar la zona horaria a España
    return sdf.format(Date())
}

fun getDayName(): String {
    val sdf = SimpleDateFormat("EEEE", Locale("es", "ES"))  // Establecer Locale a español para obtener solo el nombre del día
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")  // Configurar la zona horaria a España
    return sdf.format(Date())
}

fun getCurrentMonth(): String {
    val sdf = SimpleDateFormat("MMMM", Locale("es", "ES"))  // Establecer Locale a español
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")  // Configurar la zona horaria a España
    return sdf.format(Date())
}

fun getCurrentWeek(): String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"))  // Configurar la zona horaria a España
    cal.firstDayOfWeek = Calendar.MONDAY  // Establecer el lunes como el primer día de la semana
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)  // Establecer el primer día de la semana al lunes
    return cal.get(Calendar.WEEK_OF_YEAR).toString()  // Obtener la semana del año
}