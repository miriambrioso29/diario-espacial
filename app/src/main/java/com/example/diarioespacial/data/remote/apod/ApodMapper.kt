package com.example.diarioespacial.data.remote.apod

import com.example.diarioespacial.data.local.apod.ApodEntity

/** Valores que se usan cuando la API no envía un campo. */
private const val TITULO_POR_DEFECTO = "Sin título"
private const val EXPLICACION_POR_DEFECTO = "Sin descripción disponible."
private const val TIPO_POR_DEFECTO = "image"

/**
 * Convierte la respuesta de la API en la entidad que se guarda en Room.
 *
 * - Devuelve null si el elemento no trae fecha, porque la fecha es la clave
 *   primaria y sin ella no se puede guardar (quien llama debe descartarlo).
 * - El resto de campos obligatorios toman un valor por defecto si llegan nulos,
 *   para que la app nunca se cierre por datos incompletos.
 * - isFavorite no viene de la API: queda en false y el DAO conserva el valor guardado.
 */
fun ApodDto.toEntity(): ApodEntity? {
    val fecha = date?.takeIf { it.isNotBlank() } ?: return null
    return ApodEntity(
        date = fecha,
        title = title?.takeIf { it.isNotBlank() } ?: TITULO_POR_DEFECTO,
        explanation = explanation?.takeIf { it.isNotBlank() } ?: EXPLICACION_POR_DEFECTO,
        url = url.orEmpty(),
        hdurl = hdurl?.takeIf { it.isNotBlank() },
        mediaType = mediaType?.takeIf { it.isNotBlank() } ?: TIPO_POR_DEFECTO,
        copyright = copyright?.trim()?.takeIf { it.isNotEmpty() }
    )
}

/** Convierte una lista de DTO descartando los elementos sin fecha. */
fun List<ApodDto>.toEntities(): List<ApodEntity> = mapNotNull { it.toEntity() }
