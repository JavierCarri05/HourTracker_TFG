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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hourtracker_tfg.BDD.TurnosDataBaseHelper
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
    var showBottomSheet by remember { mutableStateOf(false) }

    // 🔥 Database helper y resumen
    val context = LocalContext.current
    val db = remember { TurnosDataBaseHelper(context) }
    var resumen by remember { mutableStateOf(db.obtenerResumenTurnos(idUsuario)) }

    // Actualiza la hora en tiempo real
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

            // Bloque de resumen: Hoy
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
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
                            Text(text = resumen.horasHoy)
                            Text(text = resumen.gananciasHoy)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bloque de resumen: Semana
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
                            Text(text = resumen.horasSemana)
                            Text(text = resumen.gananciasSemana)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bloque de resumen: Mes
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
                            Text(text = resumen.horasMes)
                            Text(text = resumen.gananciasMes)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }

        // Mostrar el BottomSheet si está activo
        if (showBottomSheet) {
            BottomShet(
                idUsuario = idUsuario,
                onDismiss = {
                    showBottomSheet = false
                    resumen = db.obtenerResumenTurnos(idUsuario) // 🔄 Refresca al cerrar la sheet
                }
            )
        }
    }
}

// --------- UTILIDADES -----------

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale("es", "ES"))
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    return sdf.format(Date())
}

fun getCurrentDay(): String {
    val sdf = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "ES"))
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    return sdf.format(Date())
}

fun getDayName(): String {
    val sdf = SimpleDateFormat("EEEE", Locale("es", "ES"))
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    return sdf.format(Date())
}

fun getCurrentMonth(): String {
    val sdf = SimpleDateFormat("MMMM", Locale("es", "ES"))
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    return sdf.format(Date())
}

fun getCurrentWeek(): String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"))
    cal.firstDayOfWeek = Calendar.MONDAY
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    return cal.get(Calendar.WEEK_OF_YEAR).toString()
}
