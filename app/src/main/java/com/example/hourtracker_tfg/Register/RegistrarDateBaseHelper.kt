package com.example.hourtracker_tfg.Register

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.hourtracker_tfg.BDD.BddHourTracker

class RegistrarDateBaseHelper (context: Context) {

    private val dbHelper = BddHourTracker(context)

    // metodo para registrar nuevo usuario
    fun nuevoUsuario(gmail: String, nombreUsuario: String, contrasena: String) {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("gmail", gmail)
            put("nombre_usuario", nombreUsuario)
            put("contrasena", contrasena)
        }
        db.insert("usuarios", null, values)
        db.close()
    }

    //Metodo para comprobar que el gmail no se repita
    fun existeGmail(gmail: String): Boolean{
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val consulta = "SELECT * FROM usuarios WHERE gmail = ?"
        val cursor: Cursor = db.rawQuery(consulta, arrayOf(gmail)) //El rawQuery ejecuta la consulta y reemplaza el ? por el gmail

        val existe = cursor.moveToFirst()
        cursor.close()
        db.close()
        return existe
    }

    fun existeNombreUsuario(nombreUsuario: String): Boolean{
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT * FROM usuarios WHERE nombre_usuario = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(nombreUsuario)) //El rawQuery ejecuta la consulta y reemplaza el ? por el nombre de usuario

        val existe = cursor.moveToFirst()
        cursor.close()
        db.close()
        return existe
    }
}