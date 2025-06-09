package com.example.hourtracker_tfg.app.Screens.sumario

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.hourtracker_tfg.BDD.TurnosDataBaseHelper
import com.example.hourtracker_tfg.BDD.TurnosDataBaseHelper.EditarTurno
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomShetEditar(
    idUsuario: Int,
    turno: EditarTurno,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val bdd = TurnosDataBaseHelper(context)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var comienzo by remember { mutableStateOf(turno.fechaInicio) }
    var fin by remember { mutableStateOf(turno.fechaFin) }
    var pausa by remember { mutableStateOf("${turno.pausa / 60}h ${turno.pausa % 60}m") }
    var tarifaPorHora by remember { mutableStateOf(String.format("%.2f", turno.tarifaHora)) }
    
    /*
    if(turno.plus == 0.0) "" else String.format("%.2f", turno.plus))
    esto si cuando edito un turno que no tiene plus
    pues que me lo muestre vacio y no 0.00
     */
    var plus by remember { mutableStateOf( if (turno.plus == 0.0) "" else String.format( "%.2f", turno.plus ) ) }
    var nota by remember { mutableStateOf(turno.nota) }
    var ganancias by remember { mutableStateOf(turno.ganancia) }

    val calendarioSeleccionada = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"))
    var fechaComienzo by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                time = SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale("es", "ES")
                ).parse(turno.fechaInicio) ?: Date()
            })
    }
    var fechaFin by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                time =
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES")).parse(turno.fechaFin)
                        ?: Date()
            })
    }

    //Esta variable es para que se pueda hacer scroll en el BottomShet
    val scroll = rememberScrollState()

    LaunchedEffect(Unit) {
        scope.launch { sheetState.show() }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = Color(0xFF121212)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ENCABEZADO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF3B82F7)
                    )
                ) {
                    Text("Cancelar")
                }

                Text(
                    text = "Editar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                TextButton(
                    onClick = {
                        //Esta variable es para comprobar si cuando edito un turno y pongo la fecha de otro que ya existe que no me deje, ya que asi no hay turnos duplicados
                        val isTurno = bdd.existeTurno(idUsuario, comienzo, fin, turno.idTurno)

                        if (isTurno) {
                            Toast.makeText(
                                context,
                                "Ya existe un turno en ese horario",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton //Esto es para que no me deje
                        }

                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
                        val fechaInicio = sdf.parse(comienzo)
                        val fechaFinal = sdf.parse(fin)

                        if (fechaFinal.before(fechaInicio)) {
                            Toast.makeText(
                                context,
                                "La fecha de fin debe ser posterior a la de inicio",
                                Toast.LENGTH_LONG
                            ).show()
                            return@TextButton
                        }

                        val pausaInt = try {
                            val parts = pausa.split("h", "m").map { it.trim() }
                            val h = parts[0].toIntOrNull() ?: 0
                            val m = parts[1].toIntOrNull() ?: 0
                            (h * 60) + m
                        } catch (e: Exception) {
                            0
                        }

                        val localeES = Locale("es", "ES")
                        val numberFormat = NumberFormat.getInstance(localeES)

                        val tarifa = try {
                            numberFormat.parse(tarifaPorHora.replace(".", ","))?.toDouble() ?: 0.0
                        } catch (e: Exception) {
                            0.0
                        }

                        val plusVal = try {
                            numberFormat.parse(plus.replace(".", ","))?.toDouble() ?: 0.0
                        } catch (e: Exception) {
                            0.0
                        }

                        if (tarifa <= 0.0) {
                            Toast.makeText(context, "Introduce la tarifa por hora", Toast.LENGTH_LONG).show()
                            return@TextButton
                        }

                        val duracionMin = ((fechaFinal.time - fechaInicio.time) / (1000 * 60)).toInt() - pausaInt
                        if(duracionMin <= 0){
                            Toast.makeText(context, "La pausa no puede ser mayor o igual que la duración del turno", Toast.LENGTH_LONG).show()
                            return@TextButton
                        }
                        val horas = duracionMin / 60.0
                        val nuevaGanancia = (horas * tarifa) + plusVal
                        ganancias = String.format("%.2f", nuevaGanancia)

                        bdd.actualizarTurno(
                            idTurno = turno.idTurno,
                            fechaInicio = comienzo,
                            fechaFin = fin,
                            pausa = pausaInt,
                            tarifaHora = tarifa,
                            plus = plusVal,
                            nota = nota
                        )

                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF3B82F7)
                    )
                ) {
                    Text("Guardar")
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(scroll)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // TIEMPO
                Text(
                    text = "TIEMPO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1C1C1E)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // COMIENZO
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Comienzo", color = Color.White)
                            TextButton(onClick = {
                                val calendario = fechaComienzo
                                DatePickerDialog(
                                    context,
                                    { _, ano, mes, dia ->
                                        TimePickerDialog(
                                            context,
                                            { _, hora, minuto ->
                                                comienzo = String.format(
                                                    "%02d/%02d/%d %02d:%02d",
                                                    dia, mes + 1, ano, hora, minuto
                                                )
                                                calendario.set(ano, mes, dia, hora, minuto)
                                                fechaComienzo = calendario
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
                                Text(comienzo, color = Color(0xFF3B82F7))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // FIN
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Fin", color = Color.White)
                            TextButton(onClick = {
                                val calendario = fechaComienzo
                                DatePickerDialog(
                                    context,
                                    { _, ano, mes, dia ->
                                        TimePickerDialog(
                                            context,
                                            { _, hora, minuto ->
                                                fin = String.format(
                                                    "%02d/%02d/%d %02d:%02d",
                                                    dia, mes + 1, ano, hora, minuto
                                                )
                                                calendario.set(ano, mes, dia, hora, minuto)
                                                fechaFin = calendario
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
                                Text(fin, color = Color(0xFF3B82F7))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // PAUSA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Pausa", color = Color.White)
                            TextButton(onClick = {
                                TimePickerDialog(context, { _, hora, minuto ->
                                    pausa = "${hora}h ${String.format("%02d", minuto)}m"
                                }, 0, 0, true).show()
                            }) {
                                Text(pausa, color = Color(0xFF3B82F7))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // GANANCIAS
                Text(
                    text = "GANANCIAS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1C1C1E)
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = tarifaPorHora,
                            onValueChange = { valor ->
                                // Solo permitir números decimales con máximo 2 decimales
//                                if (valor.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
//                                }
                                tarifaPorHora = valor
                            },
                            label = { Text("Tarifa por hora", color = Color.White) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White,
                                cursorColor = Color(0xFF3B82F7),
                                focusedBorderColor = Color(0xFF3B82F7),
                                unfocusedBorderColor = Color(0xFF3B82F7)
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = plus,
                            onValueChange = { valor ->
                                plus = valor
                            },
                            label = { Text("Plus", color = Color.White) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White,
                                cursorColor = Color(0xFF3B82F7),
                                focusedBorderColor = Color(0xFF3B82F7),
                                unfocusedBorderColor = Color(0xFF3B82F7)
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ganancias,
                            onValueChange = { valor ->
                                ganancias = valor
                            },
                            label = { Text("Ganancias", color = Color.White) },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White,
                                cursorColor = Color(0xFF3B82F7),
                                focusedBorderColor = Color(0xFF3B82F7),
                                unfocusedBorderColor = Color(0xFF3B82F7)
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // NOTA
                Text(
                    text = "NOTA",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1C1C1E)
                    )
                ) {
                    OutlinedTextField(
                        value = nota,
                        onValueChange = { nota = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            cursorColor = Color(0xFF3B82F7),
                            focusedBorderColor = Color(0xFF3B82F7),
                            unfocusedBorderColor = Color(0xFF3B82F7)
                        )
                    )
                }

                // BOTÓN ELIMINAR
                Button(
                    onClick = {
                        bdd.eliminarTurno(turno.idTurno)
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar Turno", color = Color.White)
                }
            }
        }
    }
}
