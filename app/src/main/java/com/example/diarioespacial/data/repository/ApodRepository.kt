package com.example.diarioespacial.data.repository

import com.example.diarioespacial.data.local.apod.ApodDao
import com.example.diarioespacial.data.local.apod.ApodEntity
import com.example.diarioespacial.data.remote.apod.ApodService
import com.example.diarioespacial.data.remote.apod.toEntities
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Fuente única de datos de la opción "Imágenes".
 *
 * Las pantallas leen SIEMPRE de Room a través de los Flow de este repositorio.
 * La API solo se usa para refrescar la base de datos: si falla, los datos
 * que ya estaban guardados se siguen mostrando.
 */
class ApodRepository(
    private val dao: ApodDao,
    private val servicio: ApodService
) {

    /** Todas las imágenes guardadas, de la más reciente a la más antigua. */
    val imagenes: Flow<List<ApodEntity>> = dao.observarTodas()

    /** Solo las imágenes favoritas. */
    val favoritas: Flow<List<ApodEntity>> = dao.observarFavoritas()

    /** Una imagen concreta por fecha (null si no existe). */
    fun imagen(date: String): Flow<ApodEntity?> = dao.observarPorFecha(date)

    /** Marca o desmarca una imagen como favorita. */
    suspend fun cambiarFavorita(date: String, esFavorita: Boolean) =
        dao.actualizarFavorita(date, esFavorita)

    /**
     * Descarga las imágenes de los últimos 7 días y las guarda en Room
     * conservando los favoritos. Devuelve el error en lugar de lanzarlo para
     * que el ViewModel decida qué mostrar.
     */
    suspend fun refrescar(): Result<Unit> {
        return try {
            val (inicio, fin) = rangoUltimos7Dias()
            val respuesta = servicio.obtenerImagenes(startDate = inicio, endDate = fin)
            dao.guardarConservandoFavoritos(respuesta.toEntities())
            Result.success(Unit)
        } catch (e: CancellationException) {
            // Si se cancela la corrutina (p. ej. se cierra la pantalla) no es un error real
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Calcula el rango desde hace 6 días hasta hoy (7 días en total), en formato AAAA-MM-DD.
     *
     * Se usa la zona America/New_York porque es la que usa la API: si el móvil
     * ya está en el día siguiente (p. ej. en Europa), pedir "hoy" según la hora
     * local daría una fecha futura y la API rechazaría la petición.
     */
    private fun rangoUltimos7Dias(): Pair<String, String> {
        val zonaNasa = TimeZone.getTimeZone(ZONA_HORARIA_API)
        val formato = SimpleDateFormat(FORMATO_FECHA, Locale.US).apply { timeZone = zonaNasa }
        val calendario = Calendar.getInstance(zonaNasa)

        val fin = formato.format(calendario.time)
        calendario.add(Calendar.DAY_OF_YEAR, -(DIAS_A_CONSULTAR - 1))
        val inicio = formato.format(calendario.time)

        return inicio to fin
    }

    private companion object {
        const val ZONA_HORARIA_API = "America/New_York"
        const val FORMATO_FECHA = "yyyy-MM-dd"
        const val DIAS_A_CONSULTAR = 7
    }
}
