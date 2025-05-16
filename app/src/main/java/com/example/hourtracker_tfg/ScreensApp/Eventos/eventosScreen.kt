package com.example.hourtracker_tfg.ScreensApp.Eventos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hourtracker_tfg.ScreensApp.BarraNavegacion
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

//Esta anotacion ya que como uso clase de Java Time, que no estan disponible para la API 24, esta disponible par la 26 y poniendo esa anotacion las coge
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun eventosScreen(idUsuario: Int, navController: NavController){
    val hoy = remember { LocalDate.now() } //Esta variable es para coger la decha actual del dispositivo
    var mes by remember { mutableStateOf(YearMonth.from(hoy)) }//Guardo el mes actual
    var diaSeleccionado by remember { mutableStateOf(hoy) } //Guarda el dia que ha seleccionado el usuario

    Scaffold(
        bottomBar = {
            BarraNavegacion(
                selectedItem = "Eventos",
                idUsuario = idUsuario,
                navController = navController
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            cabeceraMes(
                mesActual = mes, //Muestra el mes actual
                anterior = { mes = mes.minusMonths(1) },//Sumo un mes
                //Osea para ver el ems siguiente o el anterior
                siguiente = { mes = mes.plusMonths(1) }//Resto un mes
            )

            Spacer(modifier = Modifier.height(8.dp))

            diasSemanas()

            Spacer(modifier = Modifier.height(4.dp))

            diasMeses(
                mesActual = mes,
                diaSeleccionado = diaSeleccionado,
                fechaSeleccionada = { diaSeleccionado = it }
            )
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun cabeceraMes(
    mesActual: YearMonth,
    anterior: () -> Unit,
    siguiente: () -> Unit
){
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = anterior) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Mes Anterior",
                tint = Color.White
            )
        }

        Text(
            //Convierto el objeto YearMonth en un String. Este es el mes (Mayo 2025)
            text = mesActual.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = siguiente) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Mes siguiente",
                tint = Color.White
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun diasSemanas(){
    Row(modifier = Modifier.fillMaxWidth()) {
        //Aqui muestro los nombre de los dias de la semana. Lunes, Martes...
        for(diaSemana in DayOfWeek.values()){
            /*
            Este recorre los 7 dias de las semana de la clase
            DayOfWeek que es un enumerado que tiene los dias de la semana
             */
            Text(
                //Aqui obtengo el nombre del dia de la semana abreviado (Lunes --> LUN) y luego lo pongo en mayusculas
                text = diaSemana.getDisplayName(TextStyle.SHORT, Locale("es", "ES")).uppercase(),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun diasMeses(
    mesActual: YearMonth, //Varibale para ver el mes
    diaSeleccionado: LocalDate, //Dia que sea seleccionado
    fechaSeleccionada: (LocalDate) -> Unit //
){
    val hoy = LocalDate.now()
    val primerDiaMes = mesActual.atDay(1) //Coge el primer dia del mes
    val ultimoDiaMes = mesActual.atEndOfMonth() //Coge el ultimo dia del mes

    /*
    primerDiaMes.dayOfWeek.value nos da un indice del dia (Lunes = 1, Martes = 2...)
    le sumo 6 y luego % 7 para que el calendario empiece por lunes
     */
    val diaDeLaSemana = (primerDiaMes.dayOfWeek.value + 6) % 7

    /*
    Aqui calculo cuantas celdas necesitamos
    ya que hay dias que son "invisibles" porque el mes por ejemplo acaba en sabado y el domingo ya no
    pertenece a ese mes, entonces seria invisible
     */
    val diasTotales = diaDeLaSemana + mesActual.lengthOfMonth()
    val celdasTotales = if(diasTotales % 7 == 0) diasTotales else diasTotales

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(celdasTotales) { i ->
            /*
            Aqui recorro cada celda del calendario
            entocnes si i < diaDeLaSemana es que la celda esta vacia
            y Si i >= muestra el dia del mes
             */
            if(i < diaDeLaSemana){
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                )
            }else{
                /*
                esta parte calcula el numero del dia real
                si esta seleccionado y si es hoy
                 */
                val dia = i - diaDeLaSemana + 1
                if(dia <= ultimoDiaMes.dayOfMonth){
                    val fecha = mesActual.atDay(dia)
                    val seleccionado = diaSeleccionado.equals(fecha)
                    val esHoy = fecha.equals(hoy)

                    /*
                    ajusto el color del fondo y del texto segun si:
                    el dia que he seleccionado el fondo es rojo (para que sea mas visual el dia actual)
                    si seleccion uno que no es hoy el fondo lo pongo en blanco
                     */
                    val backgroundColor = when {
                        seleccionado && esHoy -> Color.Red
                        seleccionado -> Color.White
                        else -> Color.Transparent
                    }
                    /*
                    Ajusto el color del texto
                    si el dia que he seleccion es hoy pues el texto en negro (ya que el fondo es rojo)
                    tambien el dia actual lo pongo rojo para resaltarlo
                    y el dia normal en blanco
                     */
                    val textColor = when{
                        seleccionado -> Color.Black
                        esHoy -> Color.Red
                        else -> Color.White
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(backgroundColor)
                            .border(
                                width = if (seleccionado) 2.dp else 0.dp,
                                color = if (seleccionado) {
                                    if (esHoy) Color.Red else Color(0xFF006064)
                                } else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { fechaSeleccionada(fecha) }
                    ) {
                        Text(
                            text = dia.toString(),
                            textAlign = TextAlign.Center,
                            color = textColor,
                            fontWeight = if (seleccionado || esHoy) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 20.sp
                        )
                    }

                }else{
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}