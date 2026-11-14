package com.example.diarioespacial.data.remote.neows

import com.example.diarioespacial.data.local.asteroid.AsteroidEntity

/*
 * Conversión explícita de los DTOs de la API a la entidad de Room.
 * Si falta algún dato se usa un valor por defecto, de modo que la app
 * nunca se cierre por recibir información incompleta.
 */

/**
 * Convierte la respuesta del feed en una sola lista de entidades.
 * El mapa near_earth_objects (fecha -> lista) se aplana en una lista única,
 * se descartan los asteroides sin id y se eliminan ids repetidos.
 */
fun NeoFeedResponse.toEntity(): List<AsteroidEntity> =
    nearEarthObjects.orEmpty()
        .values                      // Colección de listas (una por fecha)
        .flatten()                   // Una sola lista con todos los asteroides
        .mapNotNull { it.toEntity() } // Convierte y descarta los que no tienen id
        .distinctBy { it.id }

/**
 * Convierte un asteroide individual. Devuelve null si no tiene id,
 * porque sin id no se puede guardar (es la clave primaria).
 */
fun NeoDto.toEntity(): AsteroidEntity? {
    val idValido = id?.takeIf { it.isNotBlank() } ?: return null

    // Se usa la primera aproximación de la lista (la del día consultado)
    val aproximacion = closeApproachData?.firstOrNull()
    val diametro = estimatedDiameter?.meters

    return AsteroidEntity(
        id = idValido,
        name = name?.takeIf { it.isNotBlank() } ?: "Sin nombre",
        closeApproachDate = aproximacion?.closeApproachDate ?: "",
        diameterMinMeters = diametro?.min ?: 0.0,
        diameterMaxMeters = diametro?.max ?: 0.0,
        // Velocidad y distancia llegan como texto: se convierten con toDoubleOrNull()
        velocityKmH = aproximacion?.relativeVelocity?.kilometersPerHour?.toDoubleOrNull() ?: 0.0,
        missDistanceKm = aproximacion?.missDistance?.kilometers?.toDoubleOrNull() ?: 0.0,
        isPotentiallyHazardous = isPotentiallyHazardous ?: false,
        // Los datos de la API nunca traen favoritos; lo conserva el DAO al guardar
        isFavorite = false
    )
}
