package com.example.hourtracker_tfg.ScreensApp.Inicio

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hourtracker_tfg.BDD.TurnosDataBaseHelper
import com.example.hourtracker_tfg.ScreensApp.BarraNavegacion
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HourTrackerScreen(idUsuario: Int, navController: NavController) {

    var horaActual by remember { mutableStateOf(horaActual()) }
    var diaActual by remember { mutableStateOf(diaActual()) }
    var nombreDiaActual by remember { mutableStateOf(nombreDiaActual()) }
    var meaActual by remember { mutableStateOf(mesActual()) }
    var anoActual by remember { mutableStateOf(anoActual()) }
    var isBottomShet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = remember { TurnosDataBaseHelper(context) }
    var resumen by remember { mutableStateOf(db.obtenerResumenTurnos(idUsuario)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            horaActual = horaActual()
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
                text = horaActual,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = diaActual,
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { isBottomShet = true },
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
                    containerColor = Color(0xFF1C1C1E)
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
                            text = nombreDiaActual,
                            color = Color.White
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = resumen.horasHoy, //Le paso la horas de hoy
                                color = Color.White
                            )
                            Text(
                                text = resumen.gananciasHoy, // Y aqui las ganancias
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
                            text = "nº $anoActual",
                            color = Color.White
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = resumen.horasSemana, //Le paso las horas totales de la semana
                                color = Color.White
                            )
                            Text(
                                text = resumen.gananciasSemana, // Y lo mismo con las ganancias
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
                            text = meaActual,
                            color = Color.White
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = resumen.horasMes, //Le paso las horas totales del mes
                                color = Color.White
                            )
                            Text(
                                text = resumen.gananciasMes, //Y lo mismo con las ganacias
                                color = Color(0xFF3B82F7)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }

        if (isBottomShet) {
            BottomShet(
                idUsuario = idUsuario,
                onDismiss = {
                    isBottomShet = false
                    resumen = db.obtenerResumenTurnos(idUsuario)
                }
            )
        }
    }
}


fun horaActual(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale("es", "ES"))
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    return sdf.format(Date())
}

fun diaActual(): String {
    val sdf = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "ES"))
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    return sdf.format(Date())
}

fun nombreDiaActual(): String {
    val sdf = SimpleDateFormat("EEEE", Locale("es", "ES"))
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    return sdf.format(Date())
}

fun mesActual(): String {
    val sdf = SimpleDateFormat("MMMM", Locale("es", "ES"))
    sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")
    return sdf.format(Date())
}

fun anoActual(): String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"))
    cal.firstDayOfWeek = Calendar.MONDAY
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    return cal.get(Calendar.WEEK_OF_YEAR).toString()
}
