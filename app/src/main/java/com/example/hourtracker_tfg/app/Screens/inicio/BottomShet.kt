package com.example.hourtracker_tfg.app.Screens.inicio

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hourtracker_tfg.BDD.TurnosDataBaseHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomShet(
    idUsuario: Int,
    fechaSeleccionada: String? = null,
    onDismiss: () -> Unit
) { //Le paso el id del usuario para trabajar con el
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    //Variables para almacenar las fechas y horas
    var comienzo by remember { mutableStateOf("") }
    var fin by remember { mutableStateOf("") }
    var pausa by remember { mutableStateOf("0h 00m") }

    // variables para las ganancias
    var tarifaPorHora by remember { mutableStateOf("") }
    var plus by remember { mutableStateOf("") }
    var ganancias by remember { mutableStateOf("") }

    // variable para nota
    var nota by remember { mutableStateOf("") }

    //Variable de la base de datos
    val bdd = TurnosDataBaseHelper(context)

    //Esta variable es para recuperar recuperar la fecha que ha seleccionado el usuario para añadir el comienzo de la jornada
    val calendarioSeleccionada = Calendar.getInstance(TimeZone.getTimeZone("Europa/Madrid"))
    var fechaComienzo by remember { mutableStateOf<Calendar?>(null) }
    var fechaFin by remember { mutableStateOf<Calendar?>(null) }


    // Mostrar la sheet directamente cuando se monta
    LaunchedEffect(Unit) {
        scope.launch { sheetState.show() }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = Color(0xFF121212)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Encabezado
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
                    text = "Entrada",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                TextButton(
                    onClick = {
                        if (comienzo.isNotEmpty() && fin.isNotEmpty()) {
                            val isTurno = bdd.existeTurno(idUsuario, comienzo, fin, -1)
                            if (isTurno) {
                                Toast.makeText(
                                    context,
                                    "Ya existe un turno en ese horario",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@TextButton //Esto es para que no me deje guardar si se va a repetir un turno
                            }

                            // Convertir fechas a objetos Date para comparar
                            val sdfFull = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
                            val fechaInicio = sdfFull.parse(comienzo)
                            val fechaFin = sdfFull.parse(fin)

                            if (fechaFin.before(fechaInicio)) {
                                Toast.makeText(
                                    context,
                                    "La fecha de fin debe ser posterior a la de inicio",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@TextButton
                            }
                            val pausaInt = try {
                                val parts = pausa.split("h", "m").map { it.trim() }
                                val horas = parts[0].toIntOrNull() ?: 0
                                val minutos = parts[1].toIntOrNull() ?: 0
                                (horas * 60) + minutos
                            } catch (e: Exception) {
                                0
                            }

                            //Esta validacion es para que cuando se rellenen las fechas de inicio y fin y si se añade pausa que no superior al tiempo trabajado
                            val duracionMin = ((fechaFin.time - fechaInicio.time) / (1000 * 60)).toInt() - pausaInt
                            if (duracionMin <= 0) {
                                Toast.makeText(context, "La pausa no puede ser mayor o igual que la duración del turno", Toast.LENGTH_LONG).show()
                                return@TextButton
                            }

                            val tarifa = tarifaPorHora.toDoubleOrNull() ?: 0.0
                            val plusVal = plus.toDoubleOrNull() ?: 0.0

                            bdd.insertarTurno(
                                idUsuario = idUsuario,
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
                        } else {
                            // Campos obligatorios no completados
                            Toast.makeText(
                                context,
                                "Debes seleccionar las fechas de inicio y fin",
                                Toast.LENGTH_LONG
                            ).show()
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
                        // Comienzo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Comienzo", color = Color.White)
                            TextButton(onClick = {
                                var calendario =
                                    Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"))
                                if (!fechaSeleccionada.isNullOrEmpty()) {
                                    try {
                                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
                                        val date = sdf.parse(fechaSeleccionada)
                                        val hora =
                                            Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"))
                                        if (date != null) {
                                            calendario.time = date
                                            calendario.set(
                                                Calendar.HOUR_OF_DAY,
                                                hora.get(Calendar.HOUR_OF_DAY)
                                            )
                                            calendario.set(
                                                Calendar.MINUTE,
                                                hora.get(Calendar.MINUTE)
                                            )
                                        }
                                    } catch (_: Exception) {
                                    }
                                }
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
                                                /*
                                            La siguiente variable es para que si yo en el comienzo añado
                                            una fecha que no es la actual, pues cuando añado el fin
                                            me marca el dia actual, entonces con la esta variable
                                            lo que voy a conseguir es que si yo selecciono un dia que no es
                                            el actual me lo guarda y lo recupera en fin y asi le facilitamos la vida al usuario
                                             */
                                                calendarioSeleccionada.set(
                                                    ano,
                                                    mes,
                                                    dia,
                                                    hora,
                                                    minuto
                                                )
                                                fechaComienzo = calendarioSeleccionada
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
                                Text(
                                    text = if (comienzo.isEmpty()) "Seleccionar" else comienzo,
                                    color = Color(0xFF3B82F7)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Fin
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Fin", color = Color.White)
                            TextButton(onClick = {
                                //Si la fechaComienzo no es null pues le asigno la fecha de comienzo al calendario
                                //Pero si es null le asigno la fecha actual
                                val calendario: Calendar
                                if (fechaComienzo != null) {
                                    calendario = fechaComienzo!!
                                } else {
                                    calendario =
                                        Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"))
                                }
                                DatePickerDialog(
                                    context,
                                    { _, ano, mes, dia ->
                                        TimePickerDialog(
                                            context,
                                            { _, hora, minuto ->
                                                fin = String.format(
                                                    "%02d/%02d/%d %02d:%02d", //Este formato lo pasa a dd/MM/yyyy
                                                    dia, mes + 1, ano, hora, minuto
                                                    /*
                                                Ejemplo:
                                                dia = 16, mes = 05, año = 2025, hora = 10, minuto = 30,
                                                el mes + 1 es porque
                                                en android cuando uso el Calendar.get(Calendar.MONTH)
                                                por defecto devuleve valores de 0 al 11, entonces seria 0 = Enero, 1 = Febrero...
                                                y le sumo uno para sea 1 = Enero, Febrero = 2...

                                                 */
                                                )
                                                calendarioSeleccionada.set(
                                                    ano,
                                                    mes,
                                                    dia,
                                                    hora,
                                                    minuto
                                                )
                                                fechaFin = calendarioSeleccionada
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
                                Text(
                                    text = if (fin.isEmpty()) "Seleccionar" else fin,
                                    color = Color(0xFF3B82F7)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Pausa
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Pausa", color = Color.White)
                            TextButton(onClick = {
                                /*
                            la _ la pongo para no utilizar el dialog
                            ya que es el primero parametro y como no lo necestio
                            lo susituyo por un _ y eso es un place-holder cuando hay un parametro que no te interesa

                            la hora (es la hora seleccionada con el fomrato de 24 horas)
                             y el minuto (es el minuto seleccionado)
                             */
                                TimePickerDialog(
                                    context,
                                    { _, hora, minuto ->
                                        //Lo formato para que sea asi. Ejemploo 8h 30m
                                        pausa = "${hora}h ${String.format("%02d", minuto)}m"
                                    },
                                    0, //Este 0 es la hora inical que se muestra
                                    0, //Y este el minuto inicial que se muestra
                                    true //Si es true usa un formato de 24h y si es false usa AM/PM
                                ).show()
                            }) {
                                Text(
                                    text = pausa,
                                    color = Color(0xFF3B82F7)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = tarifaPorHora,
                            onValueChange = { valor ->
                                //Con esto es que solo permita meter dos decimales
                                if (valor.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                    tarifaPorHora = valor
                                }
                            },
                            label = { Text("Tarifa por hora", color = Color.White) },
                            modifier = Modifier.fillMaxWidth(),
                            //Y esto es para el teclado numerico
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
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = plus,
                            onValueChange = { valor ->
                                //Con esto es que solo permita meter dos decimales
                                if (valor.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                    plus = valor
                                }
                            },
                            label = { Text("Plus", color = Color.White) },
                            //Y esto es para el teclado numerico
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
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ganancias,
                            onValueChange = { valor ->
                                // Solo permitir números decimales con máximo 2 decimales
                                if (valor.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                    ganancias = valor
                                }
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

                Spacer(modifier = Modifier.height(16.dp))

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
            }
        }
    }
}