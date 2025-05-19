package com.example.hourtracker_tfg.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.hourtracker_tfg.app.data.BddHourTracker

class DatabaseManager private constructor(context: Context) {
    private val dbHelper = BddHourTracker(context)
    private var database: SQLiteDatabase? = null

    init {
        // Aquí se inicializa la conexión a la base de datos
        database = dbHelper.writableDatabase
    }

    fun getDatabase(): SQLiteDatabase {
        return database!!
    }

    companion object {
        @Volatile
        private var INSTANCE: DatabaseManager? = null

        fun getInstance(context: Context): DatabaseManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseManager(context).also { INSTANCE = it }
            }
        }
    }
}