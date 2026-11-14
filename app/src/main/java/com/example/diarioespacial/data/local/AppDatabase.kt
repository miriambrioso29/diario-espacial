package com.example.diarioespacial.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.diarioespacial.data.local.apod.ApodDao
import com.example.diarioespacial.data.local.apod.ApodEntity
import com.example.diarioespacial.data.local.asteroid.AsteroidDao
import com.example.diarioespacial.data.local.asteroid.AsteroidEntity

/**
 * Base de datos Room compartida por las dos opciones de la app.
 *
 * Declara desde el principio las dos tablas ("apod" y "asteroids") para que
 * ninguno de los alumnos tenga que modificar este archivo y no haya conflictos
 * al fusionar las ramas.
 */
@Database(
    entities = [ApodEntity::class, AsteroidEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun apodDao(): ApodDao

    abstract fun asteroidDao(): AsteroidDao

    companion object {
        private const val NOMBRE_BD = "diario_espacial.db"

        @Volatile
        private var instancia: AppDatabase? = null

        /** Devuelve la única instancia de la base de datos (patrón singleton). */
        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    NOMBRE_BD
                ).build().also { instancia = it }
            }
    }
}
