package com.example.hourtracker_tfg.BDD

import android.content.Context
import com.example.hourtracker_tfg.ScreensApp.Sumario.DiaTrabajo
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
        val sql =
            "INSERT INTO turnos (id_usuario, fecha_inicio, fecha_fin, pausa, tarifa_hora, plus, nota) VALUES (?, ?, ?, ?, ?, ?, ?)"
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
    fun borrarDatos(idUsuario: Int) {
        db.delete("turnos", "id_usuario = ?", arrayOf(idUsuario.toString()))
    }

    //Funcion para calcular la media de lo que cobra por hora el usuario
    fun mediaPrecioPorHora(idUsuario: Int): Double {
        val cursor = db.rawQuery(
            //La consulta la pongo sobre """ ... """ ya que es un string en kotlin es ideal para SQL
            """
        SELECT AVG(tarifa_hora) FROM turnos WHERE id_usuario = ?
        """.trimIndent(), arrayOf(idUsuario.toString())
            //el "trimIndent" lo que hace es limpiar los margenes para que la consulta no tenga espacion innecesarios y queda mas limpia
        )
        var media = 0.0
        if (cursor.moveToFirst()) {
            //Aqui obtengo la media. esta puede ser null si no se han registrados datos
            media = cursor.getDouble(0)
        }
        cursor.close()
        return media
    }

    //Funcion para agrupar los turnos por mes y dia y calcular las horas y el dinero
    //Esta funcion devulve un map los meses y una lista de los dias de los meses
    fun jornadasPorMes(idUsuario: Int): Map<String, List<DiaTrabajo>> {
        val cursor = db.rawQuery(
            """
            SELECT fecha_inicio, fecha_fin, pausa, tarifa_hora, plus
            FROM turnos
            WHERE id_usuario = ?
            ORDER BY fecha_inicio DESC
            """.trimIndent(), arrayOf(idUsuario.toString())
            //Ordeno la consulta de manera descendente para que me muestre lo mas actual
        )

        //Parseo las fechas
        val pais = Locale("es", "ES")
        val entrada = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            pais
        ) //Esta variable la utilizo para leer la fecha de la BBDD
        val mes =
            SimpleDateFormat("MMMM yyyy", pais) //Variable para formatear la fecha en texto de mes
        val dia = SimpleDateFormat("dd/MM/yyyy", pais) //variable para agrupar por dia

        //Hago la estructura --> mes -> dia -> lista turnos

        /*
        La variable mapa nos permite agrupa de la siguiente manera:
            mes -> dia--> lista turnos
         */
        val mapa = mutableMapOf<String, MutableMap<String, MutableList<TurnoSimple>>>()

        while (cursor.moveToNext()) {
            val fechaInicio = cursor.getString(0)
            val fechaFin = cursor.getString(1)
            val pausa = cursor.getInt(2)
            val tarifaHora = cursor.getDouble(3)
            val plus = cursor.getDouble(4)

            val date = entrada.parse(fechaInicio)

            if (date != null) {
                val mes = mes.format(date).replaceFirstChar {
                    /*
                    Aqui lo que hago es remplazar el primer caracterer para que quede mejor, ya que haciendo esto
                    el mes se mostraria asi "Abril" y asi queda mejor
                     */
                    if (it.isLowerCase()) {
                        it.titlecase()
                    } else {
                        it.toString()
                    }
                }
                val dia = dia.format(date)

                //Me creo un objeto de TurnoSimple para almacenar los datos de cada turno y asi facilitar su calculo
                val turno = TurnoSimple(
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    pausa = pausa,
                    tarifaHora = tarifaHora,
                    plus = plus
                )

                /*
                Aqui lo que hago es crear una entrada para el solo si no existe
                 */
                if (!mapa.containsKey(mes)) {
                    mapa[mes] = mutableMapOf()
                }

                /*
                Aqui hago lo mismo que el if anterior
                pero en ese compruebo que ese dia en ese mes tambien existe antes de meterlo a los turnos
                y la "!!" estas quieren decir que esto no es null porque lo acabo de crear antes
                 */
                if (!mapa[mes]!!.containsKey(dia)) {
                    mapa[mes]!![dia] = mutableListOf()
                }
                //Meto el turno en la lista correcta, que es el dia correcto del mes correcto
                mapa[mes]!![dia]?.add(turno)
            }
        }
        cursor.close()

        //Ahora el mapa lo convierto a al estructura final

        //Me creo un mapa final para devolver solo lo que necesito mostrar por pantalla
        val resultado = mutableMapOf<String, List<DiaTrabajo>>()

        mapa.forEach { (mes, diasMap) ->
            //En este forEach recorro todo lo que agrupo y asi hacer los calculos (sumar las horas, sumar las ganancias y crear un dia de trabajo por cada dia)
            val listaDias = diasMap.map { (dia, turnos) ->
                var totalMinutos = 0
                var totalGanancias = 0.0

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", pais)

                turnos.forEach { turno ->
                    val comienzo = sdf.parse(turno.fechaInicio)
                    val fin = sdf.parse(turno.fechaFin)
                    if (comienzo != null && fin != null) {
                        val duracion =
                            ((fin.time - comienzo.time) / (1000 * 60)).toInt() - turno.pausa
                        val hora = duracion / 60.0
                        totalMinutos += duracion
                        totalGanancias += (hora * turno.tarifaHora) + turno.plus
                    }
                }

                DiaTrabajo(
                    fecha = dia,
                    turnosTotales = turnos.size,
                    horas = "${totalMinutos / 60}h ${totalMinutos % 60}m",
                    ganancias = String.format("%.2f €", totalGanancias)
                )
            }
            resultado[mes] = listaDias
        }
        return resultado
    }

    //Clase para almacenar cada turno antes de sumarlo
    //Y tambien la creo para qeu quede mas limpio el codigo
    data class TurnoSimple(
        val fechaInicio: String,
        val fechaFin: String,
        val pausa: Int,
        val tarifaHora: Double,
        val plus: Double
    )

    //Funcion para obtener los turnos de un dia de trabajo
    fun obtenerTurnosPorDia(idUsuario: Int, fechaDia: String): List<EditarTurno> {
        val cursor = db.rawQuery(
            """
            SELECT rowid, fecha_inicio, fecha_fin, pausa, tarifa_hora, plus, nota
            FROM turnos
            WHERE id_usuario = ? AND fecha_inicio LIKE ?
            ORDER BY fecha_inicio ASC
            """.trimIndent(), arrayOf(idUsuario.toString(), "$fechaDia%")
        )

        val turnos = mutableListOf<EditarTurno>()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val inicio = cursor.getString(1)
            val fin = cursor.getString(2)
            val pausa = cursor.getInt(3)
            val tarifa = cursor.getDouble(4)
            val plus = cursor.getDouble(5)
            val nota = cursor.getString(6)

            val comienzoJornada = sdf.parse(inicio)
            val finJornada = sdf.parse(fin)

            val duracion =
                if (comienzoJornada != null && finJornada != null) {
                    ((finJornada.time - comienzoJornada.time) / (1000 * 60)).toInt() - pausa
                } else 0

            val horasString = "${duracion / 60}h ${duracion % 60}m"
            val ganancia = ((duracion / 60.0) * tarifa) + plus
            val ganaciaString = String.format("%.2f €", ganancia)

            turnos.add(
                EditarTurno(
                    idTurno = id,
                    fechaInicio = inicio,
                    fechaFin = fin,
                    pausa = pausa,
                    tarifaHora = tarifa,
                    plus = plus,
                    nota = nota,
                    horas = horasString,
                    ganancia = ganaciaString
                )
            )
        }
        cursor.close()
        return turnos
    }

    data class EditarTurno(
        val idTurno: Int,
        val fechaInicio: String,
        val fechaFin: String,
        val pausa: Int,
        val tarifaHora: Double,
        val plus: Double,
        val nota: String,
        val horas: String,
        val ganancia: String
    )

    //Funcion para actualizar los turnos si los edito
    fun actualizarTurno(
        idTurno: Int,
        fechaInicio: String,
        fechaFin: String,
        pausa: Int,
        tarifaHora: Double,
        plus: Double,
        nota: String
    ) {
        val consulta = """
            UPDATE turnos
            SET fecha_inicio = ?, fecha_fin = ?, pausa = ?, tarifa_hora = ?, plus = ?, nota = ?
            WHERE id = ?
        """.trimIndent()

        val stmt = db.compileStatement(consulta)
        stmt.bindString(1, fechaInicio)
        stmt.bindString(2, fechaFin)
        stmt.bindLong(3, pausa.toLong())
        stmt.bindDouble(4, tarifaHora)
        stmt.bindDouble(5, plus)
        stmt.bindString(6, nota)
        stmt.bindLong(7, idTurno.toLong())
        stmt.executeUpdateDelete()
    }

    //Funcion para elimiar un turno
    fun eliminarTurno(idTurno: Int) {
        db.delete("turnos", "id = ?", arrayOf(idTurno.toString()))
    }

    /*
    Esta funcion es para validar antes de actualizar un turno
    ya que si se da el caso que me modifican un turno de un dia y lo modifican con las mismas fechas de otro turno
     */

    fun existeTurno(idUsuario: Int, fechaInicio: String, fechaFin: String, idTurno: Int): Boolean {
        val consulta =
            """
            SELECT COUNT(*) FROM turnos
            WHERE id_usuario = ? AND id != ? 
            AND ((fecha_inicio <= ? AND fecha_fin > ?) OR (fecha_inicio < ? AND fecha_fin >= ?))
            """.trimIndent()

        val cursor = db.rawQuery(consulta, arrayOf(idUsuario.toString(), idTurno.toString(),
            fechaFin, fechaInicio, fechaInicio, fechaFin
        ))

        var hayTurno = false

        if(cursor.moveToFirst()){
            hayTurno = cursor.getInt(0) > 0
        }
        cursor.close()
        return hayTurno
    }

    // Funcion que compara si dos fechas son del mismo día
    private fun mismoDia(date1: Date, date2: Date): Boolean {
        // Crear un formato que solo tenga en cuenta la fecha, sin la hora
        val sdf = SimpleDateFormat("yyyyMMdd", Locale("es", "ES"))

        // Comparar las cadenas de fecha
        return sdf.format(date1) == sdf.format(date2)
    }

}