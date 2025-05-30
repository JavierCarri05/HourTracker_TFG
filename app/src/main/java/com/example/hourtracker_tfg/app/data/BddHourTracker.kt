package com.example.hourtracker_tfg.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//Para crear la bdd esta tiene que extender de SQLiteOpenHelper
class BddHourTracker (context: Context) : SQLiteOpenHelper(context, NOMBRE_BDD, null, VERSION_BDD) {
    //Aqui defino el nombre y la version de la bdd
    companion object{
        const val NOMBRE_BDD = "HourTracker.db"
        const val VERSION_BDD = 3
    }

    //CREAR Y BORRAR TABLAS
    val CREAR_TABLA = "CREATE TABLE "
    val BORRAR_TABLA = "DROP TABLE IF EXISTS "

    //TABLA DE USUARIO
    val TABLA_USUARIOS: String = "usuarios"
    val CREAR_TABLA_USUARIOS = CREAR_TABLA + TABLA_USUARIOS +
            """( id INTEGER PRIMARY KEY AUTOINCREMENT,
            gmail TEXT NOT NULL,
            nombre_usuario TEXT NOT NULL,
            contrasena TEXT NOT NULL);"""
    val BORRAR_TABLA_USUARIOS = BORRAR_TABLA + TABLA_USUARIOS

    //TABLA TURNOS
    val TABLA_TURNOS: String = "turnos"
    val CREAR_TABLA_TURNOS = CREAR_TABLA + TABLA_TURNOS +
            """(id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_usuario INTEGER,
            fecha_inicio DATETIME NOT NULL,
            fecha_fin DATETIME NOT NULL,
            pausa INTEGER,
            tarifa_hora DECIMAL,
            plus DECIMAL,
            nota TEXT,
            FOREIGN KEY(id_usuario) REFERENCES usuarios(id));"""

    val BORRAR_TABLA_TURNOS = BORRAR_TABLA + TABLA_TURNOS

    //Tabla Eventos
    val TABLA_EVENTOS: String = "eventos"
    val CREAR_TABLA_EVENTOS = CREAR_TABLA + TABLA_EVENTOS +
            """(id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_usuario INTEGER,
            fecha_evento DATETIME NOT NULL,
            descripcion TEXT,
            FOREIGN KEY(id_usuario) REFERENCES usuarios(id));"""

    val BORRAR_TABLA_EVENTOS = BORRAR_TABLA + TABLA_EVENTOS


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREAR_TABLA_USUARIOS) //Creo la tabla
        db.execSQL(CREAR_TABLA_TURNOS)
        db.execSQL(CREAR_TABLA_EVENTOS)
        db.execSQL("""
        INSERT INTO usuarios (gmail, nombre_usuario, contrasena)
        VALUES ('admin@gmail.com', 'admin', 'Admin-1');
    """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(BORRAR_TABLA_USUARIOS) //Borro la tabla
        db.execSQL(BORRAR_TABLA_TURNOS)
        db.execSQL(BORRAR_TABLA_EVENTOS)
        onCreate(db)
    }
}