package com.example.hourtracker_tfg.ScreensApp

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hourtracker_tfg.BDD.BddHourTracker
import com.example.hourtracker_tfg.BDD.TurnosDataBaseHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomShet(idUsuario: Int, onDismiss: () -> Unit){ //Le paso el id del usuario para trabajar con el
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
                .padding(16.dp)
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
                            // Convertir fechas a objetos Date para comparar
                            val sdfFull = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
                            val fechaInicio = sdfFull.parse(comienzo)
                            val fechaFin = sdfFull.parse(fin)

                            if (fechaInicio != null && fechaFin != null && fechaFin.after(fechaInicio)) {
                                // Solo si la fecha de fin es posterior a la de inicio
                                val pausaInt = try {
                                    val parts = pausa.split("h", "m").map { it.trim() }
                                    val horas = parts[0].toIntOrNull() ?: 0
                                    val minutos = parts[1].toIntOrNull() ?: 0
                                    (horas * 60) + minutos
                                } catch (e: Exception) {
                                    0
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
                                // Mostrar mensaje de error
                                Toast.makeText(
                                    context,
                                    "La fecha de fin debe ser posterior a la fecha de inicio",
                                    Toast.LENGTH_LONG
                                ).show()
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
                            val calendar = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            comienzo = String.format(
                                                "%02d/%02d/%d %02d:%02d",
                                                day, month + 1, year, hour, minute
                                            )
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
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
                            val calendar = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            fin = String.format(
                                                "%02d/%02d/%d %02d:%02d",
                                                day, month + 1, year, hour, minute
                                            )
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
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
                            val calendar = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    pausa = "${hour}h ${String.format("%02d", minute)}m"
                                },
                                0,
                                0,
                                true
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
                        onValueChange = { tarifaPorHora = it },
                        label = { Text("Tarifa por hora", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
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
                        onValueChange = { plus = it },
                        label = { Text("Plus", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
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
                        onValueChange = { ganancias = it },
                        label = { Text("Ganancias", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
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
                    placeholder = { Text("Escribe tu nota...", color = Color.Gray) },
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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}