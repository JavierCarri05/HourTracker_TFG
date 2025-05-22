package com.example.hourtracker_tfg.app.data.helpers

import android.content.ContentValues
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hourtracker_tfg.app.data.DatabaseManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//esta data class representa un evento con fecha/hora y la descripcion
data class Evento(
    val fechaHora: LocalDateTime,
    val descripcion: String
)

class EventosDataBaseHelper(context: Context) {
    private val db = DatabaseManager.getInstance(context).getDatabase()

    /**
     * Funcion para insertar un evento si aun no existe un evento con la misma fecha y hora
     */
    fun insertarEvento(idUsuario: Int, fechaHora: LocalDateTime, descripcion: String) {
        /*
        Llamo a la funcion de "existeEvento" para comprobar si el evento
        que se va a insertar ya existe, si no existe lo insertamos
         */
        if (!existeEvento(idUsuario, fechaHora)) {
            val values = ContentValues().apply {
                put("id_usuario", idUsuario)
                put("fecha_evento", fechaHora.toString())
                put("descripcion", descripcion)
            }
            db.insert("eventos", null, values)
        }
    }

    //Funcion para comprobar si un evento existe con la fecha y hora y asi evitar duplicados
    fun existeEvento(idUsuario: Int, fechaHora: LocalDateTime): Boolean {
        val cursor = db.rawQuery(
            "SELECT 1 FROM eventos WHERE id_usuario = ? AND fecha_evento = ?",
            arrayOf(idUsuario.toString(), fechaHora.toString())
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }


    /**
     * Funcion para obtener todos los eventos de un usuario para un mes en especifico
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerEventosMes(idUsuario: Int, yearMonth: String): List<Evento> {
        val eventos = mutableListOf<Evento>()
        //Consulto todos los eventos del usuario cuyo mes y año coincidan
        val cursor = db.rawQuery(
            "SELECT fecha_evento, descripcion FROM eventos WHERE id_usuario = ? AND strftime('%Y-%m', fecha_evento) = ?",
            arrayOf(idUsuario.toString(), yearMonth)
        )

        //Recorro los resultado y lo añadimos a la lista de los eventos
        while (cursor.moveToNext()) {
            val fechaStr = cursor.getString(0) //Fecha del evento
            val descripcion = cursor.getString(1) ?: "" //Descripcion del evento
            try {
                //Parseo la fecha
                eventos.add(Evento(LocalDateTime.parse(fechaStr), descripcion))
            } catch (e: Exception) {
                val fecha = LocalDate.parse(fechaStr).atTime(12, 0)
                eventos.add(Evento(fecha, descripcion))
            }
        }
        cursor.close()
        return eventos
    }

    /**
     * Funcion para actualizar un evento con una nueva fecha/hora y una nueva descripcion
     */
    fun actualizarEvento(idUsuario: Int, fechaHora: LocalDateTime, nuevaFechaHora: LocalDateTime, nuevaDescripcion: String) {
        val values = ContentValues().apply {
            put("fecha_evento", nuevaFechaHora.toString())
            put("descripcion", nuevaDescripcion)
        }
        db.update(
            "eventos",
            values,
            "id_usuario = ? AND fecha_evento = ?",
            arrayOf(idUsuario.toString(), fechaHora.toString())
        )
    }

    /**
     * funcion para eliminar un evento por su id y por la fecha/hora
     */
    fun eliminarEvento(idUsuario: Int, fechaHora: LocalDateTime) {
        db.delete(
            "eventos",
            "id_usuario = ? AND fecha_evento = ?",
            arrayOf(idUsuario.toString(), fechaHora.toString())
        )
    }
}
