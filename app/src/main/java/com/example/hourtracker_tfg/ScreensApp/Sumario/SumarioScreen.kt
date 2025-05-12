package com.example.hourtracker_tfg.ScreensApp.Sumario

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hourtracker_tfg.BDD.TurnosDataBaseHelper
import com.example.hourtracker_tfg.ScreensApp.BarraNavegacion
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hourtracker_tfg.ScreensApp.BottomShet
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SumarioScreen(idUsuario: Int, navController: NavController) {
    //Variable para controlar la visibilidad del BottomShet
    var showBottomSheet by remember { mutableStateOf(false) }

    //Variable para la base de datos
    val context = LocalContext.current
    val db = remember { TurnosDataBaseHelper(context) }

    //Variable para almacenar la lista de las jornadas agrupas por mes
    var jornadasMes by remember { mutableStateOf(db.jornadasPorMes(idUsuario)) }

    //Variable para obtener la media de lo que cobra por hora el usuario
    var mediaPrecioHora by remember { mutableStateOf(db.mediaPrecioPorHora(idUsuario)) }

    Scaffold(
        //Le paso la barra de navegacion
        bottomBar = {
            BarraNavegacion(
                selectedItem = "Sumario", //Esto es para marcar en que pestaña estamos
                idUsuario = idUsuario,
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            //Esta es la parte superior donde esta el icono y la media por hora
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir una nueva actividad",
                        tint = Color(0xFF3B82F7),
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format("%.2f €", mediaPrecioHora),
                        fontSize = 26.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "por hora",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            //Lista agrupada por meses y los dias
            LazyColumn {
                jornadasMes.forEach { (mes, dias) -> //(mes, dias) esto es que el mes es la clave y el valor son los dias
                    item {
                        //Esta es la cabecera que muestra el mes
                        Text(
                            text = mes,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    //Items para cada dia del mes
                    /*
                    Los "dias.size" son los elementos hay que mostrar que lo elementos son los dias de ese mes
                     */
                    items(dias.size) { dia -> //El "dia" es como indice (0, 1, 2, 3, 4...)
                        val dia = dias[dia] //Aqui lo que hace es recoger el objeto DiaTrabajo correspondiente
                        val hoy = DiaActual(dia.fecha) //Aqui compruebo si es el dia actual
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable {
                                    val fecha = dia.fecha.replace("/", "-")
                                    navController.navigate("detalleTurnosScreen/$idUsuario/$fecha")
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text( //Aqui si la fecha es hoy pone la pone en rojo para que se note que es el dia actual
                                    text = formatearFecha(dia.fecha),
                                    color = if (hoy) {
                                        Color.Red
                                    } else {
                                        Color.White
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                Text(
                                    text = dia.turnosTotales.toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = dia.horas,
                                        color = Color.White
                                    )
                                    Text(
                                        text = dia.ganancias,
                                        color = Color(0xFF3B82F7)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Editar día",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    //Accion para mostrar el BottomShet
    if (showBottomSheet) {
        BottomShet(
            idUsuario = idUsuario,
            onDismiss = {
                showBottomSheet = false
                //Aqui recargo los datos si se añade una nueva actividad
                jornadasMes = db.jornadasPorMes(idUsuario)
                mediaPrecioHora = db.mediaPrecioPorHora(idUsuario)
            }
        )
    }
}

//Data Class para representar un dia en el que el usuario ha trabajado
data class DiaTrabajo(
    val fecha: String,
    val turnosTotales: Int,
    val horas: String,
    val ganancias: String
)

//Funcion para comprobar si la fecha del turno es la actual
fun DiaActual(fechaString: String): Boolean {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    return try {
        val fecha = sdf.parse(fechaString)
        val hoy = sdf.format(Date()) //Aqui cojo solo la fecha sin la hora
        sdf.format(fecha!!) == hoy //Aqui comparo si coincide
    } catch (e: Exception) {
        false //Si da error salta la excepcion
    }
}

//Funcion para poner el formato que yo quiero
fun formatearFecha(fechaString: String): String {
    val entrada = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    val date = entrada.parse(fechaString)

    //Aqui pongo mi formato de la fecha

    val formatoDias = mapOf(
        Calendar.MONDAY to "LUN",
        Calendar.TUESDAY to "MAR",
        Calendar.WEDNESDAY to "MIÉ",
        Calendar.THURSDAY to "JUE",
        Calendar.FRIDAY to "VIE",
        Calendar.SATURDAY to "SÁB",
        Calendar.SUNDAY to "DOM"
    )

    return if (date != null) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val diaNumero = SimpleDateFormat("dd", Locale("es", "ES")).format(date)
        val diaSemana = formatoDias[calendar.get(Calendar.DAY_OF_WEEK)] ?: ""

        "$diaNumero $diaSemana"
    } else {
        fechaString //Si hay alguno error la devuelvo sin formatear
    }
}