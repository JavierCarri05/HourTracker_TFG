package com.example.hourtracker_tfg.app.Screens.eventos

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hourtracker_tfg.app.Screens.components.BarraNavegacion
import com.example.hourtracker_tfg.app.data.helpers.Evento
import com.example.hourtracker_tfg.app.data.helpers.EventosDataBaseHelper
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
/*
Esta es la pantalla principal donde mostramos el calendario y los eventos para añadir, editar o eliminar
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun eventosScreen(idUsuario: Int, navController: NavController) {
    val context = LocalContext.current
    //variable para obtener la fecha y hora actual
    val ahora = remember { LocalDateTime.now() }
    //Formato de la fecha
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    //Variable que representa el mes actual mostrandolo en el calendario
    var mes by remember { mutableStateOf(YearMonth.from(ahora)) }
    //Variable para el dia que selecciona el usuario
    var diaSeleccionado by remember { mutableStateOf(ahora.toLocalDate()) }

    val eventosDB = remember { EventosDataBaseHelper(context) }

    //variable para la lista de los eventos del mes actual, esta se actualiza si cambiamos de mes
    var eventosMes by remember(mes) {
        mutableStateOf(eventosDB.obtenerEventosMes(idUsuario, mes.toString()))
    }

    var dialog by remember { mutableStateOf(false) }
    var descripcionEvento by remember { mutableStateOf("") }

    //SI estamos editando un evento o creandolo
    var editarEvento by remember { mutableStateOf(false) }

    //Esta es para la fecha del evento si esta editando
    var fechaOriginal by remember { mutableStateOf<LocalDateTime?>(null) }

    //Fecha para el nuevo evento
    var nuevaFecha by remember { mutableStateOf(ahora) }

    //Lista de los eventos al seleccionar un dia
    val eventosDelDia = eventosMes.filter { it.fechaHora.toLocalDate() == diaSeleccionado }

    Scaffold(
        bottomBar = {
            BarraNavegacion("Eventos", idUsuario, navController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editarEvento = false
                descripcionEvento = ""
                nuevaFecha = LocalDateTime.of(diaSeleccionado, LocalTime.of(12, 0))
                dialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir evento")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(8.dp)
        ) {
            //llamo a una fucnion auxiliar para mostra el mes y las flechas para moverme entre los meses
            cabeceraMes(mes, { mes = mes.minusMonths(1) }, { mes = mes.plusMonths(1) })

            Spacer(modifier = Modifier.height(8.dp))

            // fila de los nombre de los dias de la semana
            diasSemanas()
            Spacer(modifier = Modifier.height(4.dp))

            //Grils de los dias del mes con eventos
            diasMeses(
                mesActual = mes,
                diaSeleccionado = diaSeleccionado,
                fechaSeleccionada = { fecha -> diaSeleccionado = fecha },
                eventosPorDia = eventosMes.groupBy { it.fechaHora.toLocalDate() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Eventos del día", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                //Si no hay ningun evento en el dia que ha seleccionado el usuario montramos que no hay eventos
                if (eventosDelDia.isEmpty()) {
                    item {
                        Text("No hay eventos este día", color = Color.White)
                    }
                }
                //Muestro la lista de cada evento
                items(eventosDelDia.sortedByDescending { it.fechaHora }) { evento ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                editarEvento = true
                                fechaOriginal = evento.fechaHora
                                nuevaFecha = evento.fechaHora
                                descripcionEvento = evento.descripcion
                                dialog = true
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(text = evento.fechaHora.format(formatter), color = Color(0xFF64DD17))
                            Text(text = evento.descripcion, color = Color.White)
                        }
                    }
                }
            }
        }

        if (dialog) {
            //Este dialog se utiliza para añadir o editar un evento
            AlertDialog(
                onDismissRequest = { dialog = false },
                //Si pulsamos en un evento que ya existe, dialog sera para editar y si vamos añadir sera para nuevo evento
                title = { Text(if (editarEvento) "Editar evento" else "Nuevo evento") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = descripcionEvento,
                            onValueChange = { descripcionEvento = it },
                            label = { Text("Descripción") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = {
                            val calendario = Calendar.getInstance()
                            //Creo el date picker para escoger el dia del evento
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    //Y despues para elegir la hora
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            nuevaFecha = LocalDateTime.of(year, month + 1, day, hour, minute)
                                        },
                                        calendario.get(Calendar.HOUR_OF_DAY),
                                        calendario.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                calendario.get(Calendar.YEAR),
                                calendario.get(Calendar.MONTH),
                                calendario.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }) {
                            Text("Seleccionar fecha: ${nuevaFecha.format(formatter)}")
                        }
                    }
                },
                /*
                Cuando vamos a guardar un evento antes compruebo que si ese evento existe
                si existe no me dejara guardarlo
                 */
                confirmButton = {
                    Button(
                        enabled = descripcionEvento.isNotBlank(),
                        onClick = {
                            val eventoYaExiste = eventosDB.existeEvento(idUsuario, nuevaFecha)

                            //Cuando editamos un evento y seleccionamos una fecha que esta asociada a un evento, no nos deja guardarlo
                            if (!editarEvento && eventoYaExiste) {
                                Toast.makeText(context, "Ya hay un evento asignado a esa fecha y hora", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                            //Y aqui para guardar los cambios (actualizar o insertar)
                            if (editarEvento && fechaOriginal != null) {
                                eventosDB.actualizarEvento(idUsuario, fechaOriginal!!, nuevaFecha, descripcionEvento)
                                eventosMes = eventosMes
                                    .filterNot { it.fechaHora == fechaOriginal }
                                    .plus(Evento(nuevaFecha, descripcionEvento))
                            } else {
                                eventosDB.insertarEvento(idUsuario, nuevaFecha, descripcionEvento)
                                eventosMes = eventosMes + Evento(nuevaFecha, descripcionEvento)
                            }

                            dialog = false
                            descripcionEvento = ""
                        }
                    ) {
                        Text("Guardar")
                    }
                }
,
                dismissButton = {
                    Row {
                        TextButton(onClick = { dialog = false }) {
                            Text("Cancelar")
                        }
                        if (editarEvento && fechaOriginal != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                //Aqui llamos a la funcion para eliminar el evento
                                onClick = {
                                    eventosDB.eliminarEvento(idUsuario, fechaOriginal!!)
                                    eventosMes = eventosMes.filterNot { it.fechaHora == fechaOriginal }
                                    dialog = false
                                }
                            ) {
                                Text("Eliminar", color = Color.Red)
                            }
                        }
                    }
                }
            )
        }
    }
}

/*
    Funcion para mostrar la cabecera del calendario
    con el nombre del mes y el año
    y dos iconos < > para ir al mes anterior o al siguiente
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun cabeceraMes(mesActual: YearMonth, anterior: () -> Unit, siguiente: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,//Distribuyo los botones y el texto
        verticalAlignment = Alignment.CenterVertically //Centro los elementos verticalmente
    ) {
        //Este es el boton para retroceder un mes
        IconButton(onClick = anterior) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Mes anterior", tint = Color.White)
        }
        //Texto para mostrar el nombre del mes y el año
        Text(
            text = mesActual.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        //Boton para avanzar al mes siguiente
        IconButton(onClick = siguiente) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Mes siguiente", tint = Color.White)
        }
    }
}

/**
Funcion para mostrar los nombre de los dias de la semana (LUN, MAR...)
que estan en la parte superior del calendario y lo renderizo en un solo fila con el texto centrado
 * @param mesActual este es la del mes actual
 * @param anterior este ejecuta la accion de ir al mes anterior
 * @param siguienteeste ejecuta la accion de ir al mes siguiente
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun diasSemanas() {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dia in DayOfWeek.values()) {
            Text(
                text = dia.getDisplayName(TextStyle.SHORT, Locale("es", "ES")).uppercase(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

/**
Esta funcion renderiza todos lo dias del mes actual en una cuadricula de 7 columnas (osea 7 dias)
Marca el dia que hemos seleccionado, el dia actual y tambien cuantos dias hay por mes
 * @param mesActual Muestra el mes actual en el calendario
 * @param diaSeleccionado dia seleccionado
 * @param fechaSeleccionada actualiza el dia seleccionado
 * @param eventosPorDia mapa para agrupar los eventos por fechas
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun diasMeses(
    mesActual: YearMonth,
    diaSeleccionado: LocalDate,
    fechaSeleccionada: (LocalDate) -> Unit,
    eventosPorDia: Map<LocalDate, List<Evento>>
) {
    val hoy = LocalDate.now() //Dia actual
    val primerDiaMes = mesActual.atDay(1) //Coge el primer dia del mes
    val diaSemana = (primerDiaMes.dayOfWeek.value + 6) % 7 //COge los 7 dias de la semana
    val totalCeldas = diaSemana + mesActual.lengthOfMonth()//Y aqui redenrizo las celdas incluyendo las vacias

    LazyVerticalGrid(
        columns = GridCells.Fixed(7), //7 columnas para los 7 dias de la semana
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(totalCeldas) { i ->
            if (i < diaSemana) {
                //Esto rellena las celdas que estan vacias anres de comenzar el mes
                Box(Modifier.aspectRatio(1f).padding(4.dp))
            } else {
                val dia = i - diaSemana + 1
                val fecha = mesActual.atDay(dia)
                val seleccionado = diaSeleccionado == fecha
                val esHoy = fecha == hoy
                val eventosDia = eventosPorDia[fecha]?.size ?: 0

                //Aqui defino el color de fondo segun si es hora o hemos seleccionado otros dia y tabmien para cuando tienen eventos
                val fondo = when {
                    seleccionado && esHoy -> Color.Red
                    seleccionado -> Color.White
                    esHoy -> Color.Red
                    eventosDia > 0 -> Color(0xFF64DD17)
                    else -> Color.Transparent
                }

                val texto = when {
                    seleccionado || esHoy -> Color.Black
                    else -> Color.White
                }

                //Estos son los dias del mes marcado como un circulo clikable
                Column(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(fondo)
                        .clickable { fechaSeleccionada(fecha) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = dia.toString(),
                        color = texto,
                        fontWeight = if (seleccionado || esHoy) FontWeight.Bold else FontWeight.Normal
                    )
                    //Si tiene eventos ese dia, pongo el numero de los eventos que tiene ese dia
                    if (eventosDia > 0) {
                        Text(
                            text = "$eventosDia",
                            color = Color.Black,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
