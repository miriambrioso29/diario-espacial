package com.example.diarioespacial.data.local.apod

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Acceso a la tabla "apod".
 * Las consultas de lectura devuelven Flow: Room emite un valor nuevo
 * cada vez que cambia la tabla, así las pantallas se actualizan solas.
 */
@Dao
interface ApodDao {

    /** Todas las imágenes guardadas, de la más reciente a la más antigua. */
    @Query("SELECT * FROM apod ORDER BY date DESC")
    fun observarTodas(): Flow<List<ApodEntity>>

    /** Solo las imágenes marcadas como favoritas, de la más reciente a la más antigua. */
    @Query("SELECT * FROM apod WHERE isFavorite = 1 ORDER BY date DESC")
    fun observarFavoritas(): Flow<List<ApodEntity>>

    /** Una imagen concreta por su fecha (null si no está guardada). */
    @Query("SELECT * FROM apod WHERE date = :date")
    fun observarPorFecha(date: String): Flow<ApodEntity?>

    /** Marca o desmarca una imagen como favorita. */
    @Query("UPDATE apod SET isFavorite = :esFavorita WHERE date = :date")
    suspend fun actualizarFavorita(date: String, esFavorita: Boolean)

    /** Fechas de las imágenes que ya están marcadas como favoritas. */
    @Query("SELECT date FROM apod WHERE isFavorite = 1")
    suspend fun obtenerFechasFavoritas(): List<String>

    /** Inserta las imágenes nuevas y reemplaza las existentes con la misma fecha. */
    @Upsert
    suspend fun upsert(imagenes: List<ApodEntity>)

    /** Borra todas las imágenes que NO están marcadas como favoritas. */
    @Query("DELETE FROM apod WHERE isFavorite = 0")
    suspend fun borrarNoFavoritas()

    /**
     * Reemplaza los datos guardados por los recibidos de la API SIN perder favoritos.
     *
     * 1. Se apuntan las fechas que estaban marcadas como favoritas.
     * 2. Se borran las imágenes que no son favoritas: así desaparecen los datos
     *    viejos (por ejemplo, el contenido genérico "NASA Science" de la API antigua)
     *    aunque su fecha ya no venga en la respuesta.
     * 3. Se guardan las nuevas copiando el isFavorite que tenían. Si una favorita
     *    viene en la respuesta, su contenido se actualiza y sigue siendo favorita.
     *
     * Todo ocurre en una transacción: si algo falla no se borra nada, y Room
     * emite un único cambio, así la lista no parpadea vacía.
     */
    @Transaction
    suspend fun reemplazarConservandoFavoritos(imagenes: List<ApodEntity>) {
        val favoritas = obtenerFechasFavoritas().toSet()
        borrarNoFavoritas()
        upsert(imagenes.map { it.copy(isFavorite = it.date in favoritas) })
    }
}
