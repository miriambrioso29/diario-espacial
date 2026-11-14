package com.example.diarioespacial.data.remote.apod

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Endpoints de la API APOD.
 *
 * La NASA migró APOD a science.nasa.gov (la API antigua, api.nasa.gov/planetary/apod,
 * devuelve contenido genérico y se apaga el 1 de diciembre de 2026). Como el dominio
 * es distinto de la URL base de RetrofitInstance, en @GET se pone la URL completa.
 * El parámetro api_key lo sigue agregando automáticamente el interceptor de RetrofitInstance.
 */
interface ApodService {

    /**
     * Devuelve las imágenes del día más recientes, de la más nueva a la más antigua.
     *
     * OJO: en noviembre de 2026 la nueva API IGNORA start_date y end_date y siempre
     * devuelve las últimas publicaciones. Se envían igualmente por si la NASA empieza
     * a respetarlos, pero el repositorio filtra el rango de fechas por su cuenta.
     *
     * @param startDate fecha inicial en formato AAAA-MM-DD.
     * @param endDate fecha final en formato AAAA-MM-DD.
     * @param porPagina cuántas publicaciones pedir (parámetro per_page de WordPress).
     * @param campos campos que se piden (parámetro _fields de WordPress). Sin él cada
     *   elemento incluye la página HTML completa y la respuesta pesa unas 4 veces más.
     */
    @GET("https://science.nasa.gov/wp-json/wp/v2/apod-basic")
    suspend fun obtenerImagenes(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("per_page") porPagina: Int = 10,
        @Query("_fields") campos: String = "date,title,explanation,url,hdurl,media_type,copyright"
    ): List<ApodDto>
}
