package com.example.hourtracker_tfg.app.data.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.hourtracker_tfg.app.data.DatabaseManager

class LoginDateBaseHelper (context: Context) {
    //Aqui obtengo la instancia que he creado del DataBaseManager
    val db = DatabaseManager.getInstance(context).getDatabase()

    fun comprobarUsuario(nombreUsuario: String, contrasena: String): Boolean{
        val consulta = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?"
        val cursor: Cursor = db.rawQuery(consulta, arrayOf(nombreUsuario, contrasena))

        val existeUsuario = cursor.moveToFirst()
        cursor.close()
        return existeUsuario
    }

    fun cambiarContrasena(nombreUsuario: String, nuevaContrasena: String): Boolean{

        /*
        Me creo un objeto de tipo "ContentValues" que este nos permite almacenar un conjunto de valores clave (Clave:Valor)
        que en esta caso en el campo "contrasena" que es el clave y el valor es la nueva contrasena
         */
        val contentValue = ContentValues().apply {
            put("contrasena", nuevaContrasena)
        }

        // Aqui ya cambio la contraseña con el db.update que lo que hago es:
        /*
        El db.update lo que hace es actualizar la contraseña pero primero lo que hace es
        buscar en la tabla "usuarios" el registro cuyo nombre_usuario coincida con el nombre de usuario
        que se le pasa por parametro y una vez que haya encontrado ese usuario actuliza el campo contrasena con el valor de nuevaContrasena
         */
        val actualizarContrasena = db.update("usuarios", contentValue, "nombre_usuario = ?", arrayOf(nombreUsuario))

        //Si es mayor que 0 quiere decir que se han hecho cambios
        return actualizarContrasena > 0
    }

    // Función para obtener el ID del usuario
    fun obtenerIdUsuario(nombreUsuario: String): Int {
        val consulta = "SELECT id FROM usuarios WHERE nombre_usuario = ?"
        val cursor: Cursor = db.rawQuery(consulta, arrayOf(nombreUsuario))
        val idUsuario = if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex("id")
            if (index != -1) cursor.getInt(index) else 0
        } else {
            0
        }
        cursor.close()
        return idUsuario
    }

}