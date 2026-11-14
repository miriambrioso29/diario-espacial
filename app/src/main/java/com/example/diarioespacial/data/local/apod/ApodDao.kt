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

    /**
     * Guarda las imágenes recibidas de la API SIN perder el estado de favorito.
     *
     * Los datos que llegan de la API siempre traen isFavorite = false, así que
     * antes de guardarlos se copia el valor que ya estaba en la base de datos.
     * Se hace dentro de una transacción para que nadie cambie un favorito
     * entre la lectura y la escritura.
     */
    @Transaction
    suspend fun guardarConservandoFavoritos(imagenes: List<ApodEntity>) {
        val favoritas = obtenerFechasFavoritas().toSet()
        upsert(imagenes.map { it.copy(isFavorite = it.date in favoritas) })
    }
}
