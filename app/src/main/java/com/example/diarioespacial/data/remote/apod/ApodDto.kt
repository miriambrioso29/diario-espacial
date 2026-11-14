package com.example.diarioespacial.data.remote.apod

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API APOD (Astronomy Picture of the Day) para un día.
 *
 * Todos los campos son nullable porque la API no siempre los envía
 * (por ejemplo, copyright o hdurl faltan en muchas imágenes).
 * Cada campo lleva @SerializedName para que Gson los encuentre también
 * en la versión release optimizada (R8 renombra los campos sin anotación).
 */
data class ApodDto(
    @SerializedName("date") val date: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("explanation") val explanation: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("hdurl") val hdurl: String? = null,
    @SerializedName("media_type") val mediaType: String? = null,
    @SerializedName("copyright") val copyright: String? = null
)
