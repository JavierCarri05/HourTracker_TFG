package com.example.hourtracker_tfg.app.Screens.sumario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hourtracker_tfg.BDD.TurnosDataBaseHelper
import com.example.hourtracker_tfg.BDD.TurnosDataBaseHelper.EditarTurno
import com.example.hourtracker_tfg.app.Screens.components.BarraNavegacion
import com.example.hourtracker_tfg.app.Screens.inicio.BottomShet
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DetalleTurnosScreen(
    idUsuario: Int,
    fecha: String,
    navController: NavController
) {
    //Variables de la base de datos
    val context = LocalContext.current
    val db = remember { TurnosDataBaseHelper(context) }

    //Esta variable es para cargar los turnos de ese dia
    var turnos by remember { mutableStateOf(db.obtenerTurnosPorDia(idUsuario, fecha)) }

    //Variable para mostrar el bottomShet para añadir una nueva actividad
    var showAddShet by remember { mutableStateOf(false) }

    var seleccionarTurno by remember { mutableStateOf<EditarTurno?>(null) }

    //Variable para calcular el total del dia
    val (totalHoras, totalGanancias) = totalDia(turnos)

    Scaffold(
        bottomBar = {
            BarraNavegacion(
                selectedItem = "Sumario",
                idUsuario = idUsuario,
                navController = navController
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            //Esta es la fila del icono de volver y el de añadir
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Volver atras",
                        tint = Color(0xFF3B82F7),
                        modifier = Modifier.size(40.dp)
                    )
                }
                IconButton(onClick = { showAddShet = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nueva Actividad",
                        tint = Color(0xFF3B82F7),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            //Aqui va la lista de los turnos de ese dia
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(turnos.size) { t ->
                    val turno = turnos[t]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                seleccionarTurno = turno
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${turno.horaInicio()} - ${turno.horaFin()}",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = turno.horas,
                                    color = Color(0xFFF2A33C),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = turno.ganancia,
                                    color = Color(0xFF68CE67),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            //Esta es la barra donde sale el total de las ganancias y del dinero

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = totalGanancias,
                    color = Color(0xFF68CE67),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = totalHoras,
                    color = Color(0xFFF2A33C),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
    if (showAddShet) {
        BottomShet(
            idUsuario = idUsuario,
            fechaSeleccionada = fecha,
            onDismiss = {
                showAddShet = false
                turnos = db.obtenerTurnosPorDia(idUsuario, fecha)
            }
        )
    }

    if (seleccionarTurno != null) {
        BottomShetEditar(
            idUsuario = idUsuario,
            turno = seleccionarTurno!!,
            onDismiss = {
                seleccionarTurno = null
                turnos = db.obtenerTurnosPorDia(idUsuario, fecha)
            }
        )
    }
}

//funcion para calcular el total del dia. Tanto las hora como las ganancias
fun totalDia(turnos: List<EditarTurno>): Pair<String, String> {
    var minutosTotales = 0
    var dineroTotal = 0.0

    turnos.forEach { turno ->
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
        val inicio = sdf.parse(turno.fechaInicio)
        val fin = sdf.parse(turno.fechaFin)

        if(inicio != null && fin != null){
            val duracionMin = ((fin.time - inicio.time) / (1000 * 60)).toInt() - turno.pausa
            val horas = duracionMin / 60.0
            minutosTotales += duracionMin
            dineroTotal += (horas * turno.tarifaHora) + turno.plus
        }
    }

    val horasString = "${minutosTotales / 60}h ${minutosTotales % 60}m"
    val gananciasString = String.format("%.2f €", dineroTotal)
    return horasString to gananciasString
}

//Funcion para obtener solo la hora de inicio
fun EditarTurno.horaInicio(): String {
    return this.fechaInicio.split(" ").getOrNull(1) ?: "--:--"
}

//Funcion para obtener solo la hora de fin
fun EditarTurno.horaFin(): String {
    return this.fechaFin.split(" ").getOrNull(1) ?: "--:--"
}