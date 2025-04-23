package com.example.hourtracker_tfg.BDD

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
//Para crear la bdd esta tiene que extender de SQLiteOpenHelper
class BddHourTracker (context: Context) : SQLiteOpenHelper(context, NOMBRE_BDD, null, VERSION_BDD) {
    //Aqui defino el nombre y la version de la bdd
    companion object{
        const val NOMBRE_BDD = "HourTracker.db"
        const val VERSION_BDD = 1
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


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREAR_TABLA_USUARIOS) //Creo la tabla
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(BORRAR_TABLA_USUARIOS) //Borro la tabla
        onCreate(db)
    }
}