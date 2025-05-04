package com.example.hourtracker_tfg.BDD

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

data class ResumenTurno(
    val horasHoy: String,
    val gananciasHoy: String,
    val horasSemana: String,
    val gananciasSemana: String,
    val horasMes: String,
    val gananciasMes: String
)

class TurnosDataBaseHelper(context: Context) {
    private val db = DatabaseManager.getInstance(context).getDatabase()
    //Funcion para insertar un turno
    fun insertarTurno(
        idUsuario: Int,
        fechaInicio: String,
        fechaFin: String,
        pausa: Int,
        tarifaHora: Double,
        plus: Double,
        nota: String
    ) {
        val sql = "INSERT INTO turnos (id_usuario, fecha_inicio, fecha_fin, pausa, tarifa_hora, plus, nota) VALUES (?, ?, ?, ?, ?, ?, ?)"
        val stmt = db.compileStatement(sql)
        stmt.bindLong(1, idUsuario.toLong())
        stmt.bindString(2, fechaInicio)
        stmt.bindString(3, fechaFin)
        stmt.bindLong(4, pausa.toLong())
        stmt.bindDouble(5, tarifaHora)
        stmt.bindDouble(6, plus)
        stmt.bindString(7, nota)
        stmt.executeInsert()
    }

    //Funcion para obtener el total de hora y ganancias
    fun obtenerResumenTurnos(idUsuario: Int): ResumenTurno {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("es", "ES"))
        val hoy = Date()

        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val semanaStart = cal.time

        cal.set(Calendar.DAY_OF_MONTH, 1)
        val mesStart = cal.time

        val cursor = db.rawQuery(
            """
            SELECT fecha_inicio, fecha_fin, pausa, tarifa_hora, plus
            FROM turnos
            WHERE id_usuario = ?
            """.trimIndent(), arrayOf(idUsuario.toString())
        )

        var horasHoy = 0
        var totalDineroHoy = 0.0
        var horasSemana = 0
        var totalDineroSemana = 0.0
        var horasMes = 0
        var totalDineroMes = 0.0

        while (cursor.moveToNext()) {
            val fechaInicio = cursor.getString(0)
            val fechaFin = cursor.getString(1)
            val pausa = cursor.getInt(2)
            val tarifaHora = cursor.getDouble(3)
            val plus = cursor.getDouble(4)

            val sdfFull = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
            val comienzo = sdfFull.parse(fechaInicio)
            val fin = sdfFull.parse(fechaFin)

            if (comienzo != null && fin != null) {
                val duracion = ((fin.time - comienzo.time) / (1000 * 60)).toInt() - pausa
                val horas = duracion / 60.0
                val ganancia = (horas * tarifaHora) + plus

//                // Añadimos logs para depuración
//                println("Fecha del turno: ${sdfFull.format(comienzo)}")
//                println("Fecha actual: ${sdfFull.format(hoy)}")
//                println("¿Es mismo día?: ${mismoDia(comienzo, hoy)}")

                if (mismoDia(comienzo, hoy)) {
                    horasHoy += duracion
                    totalDineroHoy += ganancia
//                    println("Añadiendo al día de hoy: $duracion minutos, $ganancia €")
                }

                if (!comienzo.before(semanaStart)) {
                    horasSemana += duracion
                    totalDineroSemana += ganancia
                }

                if (!comienzo.before(mesStart)) {
                    horasMes += duracion
                    totalDineroMes += ganancia
                }
            }
        }
        cursor.close()

        return ResumenTurno(
            horasHoy = "${horasHoy / 60}h ${horasHoy % 60}m",
            gananciasHoy = String.format("%.2f €", totalDineroHoy),
            horasSemana = "${horasSemana / 60}h ${horasSemana % 60}m",
            gananciasSemana = String.format("%.2f €", totalDineroSemana),
            horasMes = "${horasMes / 60}h ${horasMes % 60}m",
            gananciasMes = String.format("%.2f €", totalDineroMes)
        )
    }

    //Metodo para borrar los datos de los turnos del usuario
    fun borrarDatos(idUsuario: Int){
        db.delete("turnos","id_usuario = ?", arrayOf(idUsuario.toString()))
    }

    // Función mejorada que compara si dos fechas son del mismo día
    private fun mismoDia(date1: Date, date2: Date): Boolean {
        // Crear un formato que solo tenga en cuenta la fecha, sin la hora
        val sdf = SimpleDateFormat("yyyyMMdd", Locale("es", "ES"))

        // Comparar las cadenas de fecha (esto elimina la parte de la hora)
        return sdf.format(date1) == sdf.format(date2)
    }
}