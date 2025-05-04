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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hourtracker_tfg.BDD.TurnosDataBaseHelper
import com.example.hourtracker_tfg.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HourTrackerScreen(idUsuario: Int, navController: NavController) {
    val icon = painterResource(id = R.drawable.time)
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var currentDay by remember { mutableStateOf(getCurrentDay()) }
    var currentDayName by remember { mutableStateOf(getDayName()) }
    var currentMonth by remember { mutableStateOf(getCurrentMonth()) }
    var currentWeek by remember { mutableStateOf(getCurrentWeek()) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = remember { TurnosDataBaseHelper(context) }
    var resumen by remember { mutableStateOf(db.obtenerResumenTurnos(idUsuario)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = getCurrentTime()
        }
    }

    Scaffold(
        bottomBar = {
            BarraNavegacion(
                selectedItem = "Inicio",
                idUsuario = idUsuario,
                navController = navController
            )
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
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = currentTime,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = currentDay,
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showBottomSheet = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F7)
                )
            ) {
                Text("Nueva Actividad")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1C1C1E) // 🔳 Fondo gris oscuro
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Hoy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hoy",
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = currentDayName,
                            color = Color.White
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = resumen.horasHoy,
                                color = Color.White
                            )
                            Text(
                                text = resumen.gananciasHoy,
                                color = Color(0xFF3B82F7)
                            )
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
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = "nº $currentWeek",
                            color = Color.White
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = resumen.horasSemana,
                                color = Color.White
                            )
                            Text(
                                text = resumen.gananciasSemana,
                                color = Color(0xFF3B82F7)
                            )
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
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = currentMonth,
                            color = Color.White
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = resumen.horasMes,
                                color = Color.White
                            )
                            Text(
                                text = resumen.gananciasMes,
                                color = Color(0xFF3B82F7)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }

        if (showBottomSheet) {
            BottomShet(
                idUsuario = idUsuario,
                onDismiss = {
                    showBottomSheet = false
                    resumen = db.obtenerResumenTurnos(idUsuario)
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
