import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.hourtracker_tfg.BDD.BddHourTracker

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

    // No cierres la base de datos aquí
//    fun close() {
//         Solo ciérrala cuando realmente la necesites cerrar (por ejemplo, cuando la aplicación se detiene)
//        database?.close()
//        database = null
//    }

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
