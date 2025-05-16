package com.example.hourtracker_tfg.Registro

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class RegistroDateBaseHelper (context: Context) {

    private val db = DatabaseManager.getInstance(context).getDatabase()

    // metodo para registrar nuevo usuario
    fun nuevoUsuario(gmail: String, nombreUsuario: String, contrasena: String) {
        val values = ContentValues().apply {
            put("gmail", gmail)
            put("nombre_usuario", nombreUsuario)
            put("contrasena", contrasena)
        }
        db.insert("usuarios", null, values)
    }

    //Metodo para comprobar que el gmail no se repita
    fun existeGmail(gmail: String): Boolean{
        val consulta = "SELECT * FROM usuarios WHERE gmail = ?"
        val cursor: Cursor = db.rawQuery(consulta, arrayOf(gmail)) //El rawQuery ejecuta la consulta y reemplaza el ? por el gmail

        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    fun existeNombreUsuario(nombreUsuario: String): Boolean{
        val query = "SELECT * FROM usuarios WHERE nombre_usuario = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(nombreUsuario)) //El rawQuery ejecuta la consulta y reemplaza el ? por el nombre de usuario

        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }
    // Función para obtener el ID después de registrar un nuevo usuario
    fun obtenerIdUsuario(nombreUsuario: String): Int {
        val consulta = "SELECT id FROM usuarios WHERE nombre_usuario = ?"
        val cursor: Cursor = db.rawQuery(consulta, arrayOf(nombreUsuario))
        val idUsuario = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndex("id")) else 0
        cursor.close()
        return idUsuario
    }



}
