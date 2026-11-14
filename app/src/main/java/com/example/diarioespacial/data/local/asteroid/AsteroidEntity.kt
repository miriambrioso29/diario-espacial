package com.example.diarioespacial.data.local.asteroid

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Asteroide cercano a la Tierra (NeoWs) guardado localmente.
 * Los diámetros se guardan en metros, la velocidad en km/h y la distancia en km.
 */
@Entity(tableName = "asteroids")
data class AsteroidEntity(
    @PrimaryKey val id: String,
    val name: String,
    val closeApproachDate: String,
    val diameterMinMeters: Double,
    val diameterMaxMeters: Double,
    val velocityKmH: Double,
    val missDistanceKm: Double,
    val isPotentiallyHazardous: Boolean,
    val isFavorite: Boolean = false
)
