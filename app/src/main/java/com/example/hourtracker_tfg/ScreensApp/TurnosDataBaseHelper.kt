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
    /**
     * 1. Esta funcion reciber ciertos parametros para poder insertalos en la tabla de turnos
     * 2. El sql contiene la consulta para poder insertar los turnos, los ? son marcadores de posicion para los valores que se van a insertar
     * 3. me creo una "compileStatement" ya que esta me ayuda a prepara la consulta, compilarala y optimizar su ejecucion
     * Ademas usando el compileStatement es mas seguro ya que asi puedo evitar las inyecciones SQL
     * 4. En este punto lo que estoy haciendo es vincular cada uno de los parametos a la consulta con el Bind y por ultimo ejecuto la consulta
     */
    fun insertarTurno( //1.
        idUsuario: Int,
        fechaInicio: String,
        fechaFin: String,
        pausa: Int,
        tarifaHora: Double,
        plus: Double,
        nota: String
    ) {
        //2.
        val sql = "INSERT INTO turnos (id_usuario, fecha_inicio, fecha_fin, pausa, tarifa_hora, plus, nota) VALUES (?, ?, ?, ?, ?, ?, ?)"
        //3.
        val stmt = db.compileStatement(sql)
        //4.
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
        //Me declaro el "hoy" para obtener el dia actual
        val hoy = Date()

        /**
         * - cal: hago una instancia de Calendar para poder manipular las fechas mas facil
         * - semanaStart: configuro el calendario para que empiece por el lunes
         * mesStart: configuro el calendario para que sea el primero dia del mes y tamvien obtengo la fecha
         */
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val semanaStart = cal.time

        cal.set(Calendar.DAY_OF_MONTH, 1)
        val mesStart = cal.time

        val cursor = db.rawQuery(
            /**
             * el cursor ejecuta la consulta con el rawQuery para obtener los registros de la tabla "turnos"
             * donde el "id_usuario" coincide con el idUsuario
             * Y tambien recupero los valores como son, fechaInicio, fechaFin...
             */

            """
            SELECT fecha_inicio, fecha_fin, pausa, tarifa_hora, plus
            FROM turnos
            WHERE id_usuario = ?
            """.trimIndent(), arrayOf(idUsuario.toString())
        )

        /**
         * Me declaro estas variable almacenar las horas y las ganancias del dia de hoy, semana y mes
         * Y estas se van acomulando, osea su valor sera mas alto si el usuario registra mas turnos
         */
        var horasHoy = 0
        var totalDineroHoy = 0.0
        var horasSemana = 0
        var totalDineroSemana = 0.0
        var horasMes = 0
        var totalDineroMes = 0.0

        /**
         * hago un bucle para iterar los resultado del cursor
         * y asi btener los valors de cada columna del registro actual
         */
        while (cursor.moveToNext()) {
            val fechaInicio = cursor.getString(0)
            val fechaFin = cursor.getString(1)
            val pausa = cursor.getInt(2)
            val tarifaHora = cursor.getDouble(3)
            val plus = cursor.getDouble(4)

            /**
             * Convierto las fecha a un formato mas detallado
             * El comienzo y el fin se convierten las cadenas de las fecha
             * la durancion calcular lo que ha durado el turno que es (fin - comienzo) - pausa
             * luego lo convierto a horas y en las ganancias calculo el dinero que ha generado el usuario
             */
            val sdfFull = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
            val comienzo = sdfFull.parse(fechaInicio)
            val fin = sdfFull.parse(fechaFin)

            if (comienzo != null && fin != null) {
                /**
                 * Saco la duracion del turno en minutos
                 * Luego las horas hago la division de la duracion / 60.o para convertir los minutos en horas
                 * y lugo calculo la ganancia
                 */
                val duracion = ((fin.time - comienzo.time) / (1000 * 60)).toInt() - pausa
                val horas = duracion / 60.0
                val ganancia = (horas * tarifaHora) + plus

                /**
                 * Llamo a la funcion "mismoDia" para verificar si el turno corresponde al dia de hoy
                 * Si coincice pues sumo las horas y las ganancias para que aparezca en el card donde esta el resumen de las horas y de las ganancias
                 */
                if (mismoDia(comienzo, hoy)) {
                    horasHoy += duracion
                    totalDineroHoy += ganancia
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
            //Aqui calculo y lo paso al resumento
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

        // Comparar las cadenas de fecha
        return sdf.format(date1) == sdf.format(date2)
    }
}