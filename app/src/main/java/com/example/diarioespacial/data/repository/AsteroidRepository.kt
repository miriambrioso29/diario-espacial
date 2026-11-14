package com.example.diarioespacial.data.repository

import com.example.diarioespacial.data.local.asteroid.AsteroidDao
import com.example.diarioespacial.data.local.asteroid.AsteroidEntity
import com.example.diarioespacial.data.remote.neows.NeoWsService
import com.example.diarioespacial.data.remote.neows.toEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Repositorio de asteroides: única fuente de datos para la UI.
 *
 * - Las pantallas leen SIEMPRE desde Room (los Flow del DAO).
 * - [refrescar] consulta la API de los últimos 7 días y guarda el resultado en Room;
 *   como la UI observa Room, se actualiza sola.
 * - Si la API falla, Room conserva los datos anteriores y se siguen mostrando.
 */
class AsteroidRepository(
    private val dao: AsteroidDao,
    private val api: NeoWsService
) {

    /** Todos los asteroides guardados. */
    val asteroides: Flow<List<AsteroidEntity>> = dao.obtenerTodos()

    /** Solo los potencialmente peligrosos. */
    val peligrosos: Flow<List<AsteroidEntity>> = dao.obtenerPeligrosos()

    /** Los marcados como favoritos ("Mis asteroides"). */
    val favoritos: Flow<List<AsteroidEntity>> = dao.obtenerFavoritos()

    /** Un asteroide concreto por su id. */
    fun asteroide(id: String): Flow<AsteroidEntity?> = dao.obtenerPorId(id)

    /** Marca o desmarca un asteroide como favorito. */
    suspend fun cambiarFavorito(id: String, esFavorito: Boolean) =
        dao.actualizarFavorito(id, esFavorito)

    /**
     * Consulta la API para los últimos 7 días (hoy menos 6 días hasta hoy)
     * y guarda los asteroides en Room sin perder los favoritos.
     *
     * @return Result.success con la cantidad guardada, o Result.failure si la API falló.
     */
    suspend fun refrescar(): Result<Int> = try {
        val (inicio, fin) = rangoUltimos7Dias()
        val entidades = api.getFeed(startDate = inicio, endDate = fin).toEntity()
        dao.guardarConservandoFavoritos(entidades)
        Result.success(entidades.size)
    } catch (e: kotlin.coroutines.cancellation.CancellationException) {
        throw e // No se debe capturar la cancelación de la corrutina
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Devuelve (fecha inicial, fecha final) en formato AAAA-MM-DD: hoy - 6 días y hoy. */
    private fun rangoUltimos7Dias(): Pair<String, String> {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendario = Calendar.getInstance()
        val fin = formato.format(calendario.time)
        calendario.add(Calendar.DAY_OF_YEAR, -6)
        val inicio = formato.format(calendario.time)
        return inicio to fin
    }
}
