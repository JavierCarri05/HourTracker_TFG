package com.example.hourtracker_tfg.app.core

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("HourTrackerSession", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "id_usuario"
    }

    // Guardar el ID del usuario
    fun guardarIdUsuario(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    // Obtener el id del usuario, si es null es que no se ha iniciado sesion
    fun getUserId(): Int? {
        val id = prefs.getInt(KEY_USER_ID, -1)
        return if (id != -1) id else null
    }

    // Cerrar sision
    fun cerrarSesion() {
        prefs.edit().remove(KEY_USER_ID).apply()
    }
}