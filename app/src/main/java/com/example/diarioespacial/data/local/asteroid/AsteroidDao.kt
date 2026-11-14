package com.example.diarioespacial.data.local.asteroid

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Acceso a la tabla "asteroids".
 * Las consultas de lectura devuelven Flow: Room vuelve a emitir
 * automáticamente cada vez que la tabla cambia, así la UI siempre
 * muestra lo que hay guardado en la base de datos.
 */
@Dao
interface AsteroidDao {

    /** Todos los asteroides, del más reciente al más antiguo por fecha de aproximación. */
    @Query("SELECT * FROM asteroids ORDER BY closeApproachDate DESC, name ASC")
    fun obtenerTodos(): Flow<List<AsteroidEntity>>

    /** Solo los asteroides potencialmente peligrosos. */
    @Query("SELECT * FROM asteroids WHERE isPotentiallyHazardous = 1 ORDER BY closeApproachDate DESC, name ASC")
    fun obtenerPeligrosos(): Flow<List<AsteroidEntity>>

    /** Solo los asteroides marcados como favoritos ("Mis asteroides"). */
    @Query("SELECT * FROM asteroids WHERE isFavorite = 1 ORDER BY closeApproachDate DESC, name ASC")
    fun obtenerFavoritos(): Flow<List<AsteroidEntity>>

    /** Un asteroide por su id (emite null si no existe). */
    @Query("SELECT * FROM asteroids WHERE id = :id")
    fun obtenerPorId(id: String): Flow<AsteroidEntity?>

    /** Marca o desmarca un asteroide como favorito. */
    @Query("UPDATE asteroids SET isFavorite = :esFavorito WHERE id = :id")
    suspend fun actualizarFavorito(id: String, esFavorito: Boolean)

    /** Ids de los asteroides que el usuario marcó como favoritos. */
    @Query("SELECT id FROM asteroids WHERE isFavorite = 1")
    suspend fun obtenerIdsFavoritos(): List<String>

    /** Inserta o reemplaza asteroides. Uso interno: preferir [guardarConservandoFavoritos]. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(asteroides: List<AsteroidEntity>)

    /**
     * Guarda los asteroides descargados de la API SIN perder los favoritos:
     * antes de reemplazar, se leen los ids marcados y se vuelve a poner
     * isFavorite = true a los que ya lo eran. Se ejecuta en una transacción.
     */
    @Transaction
    suspend fun guardarConservandoFavoritos(asteroides: List<AsteroidEntity>) {
        val idsFavoritos = obtenerIdsFavoritos().toSet()
        insertarTodos(asteroides.map { it.copy(isFavorite = it.id in idsFavoritos) })
    }
}
