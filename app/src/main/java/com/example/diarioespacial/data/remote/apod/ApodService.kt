package com.example.diarioespacial.data.remote.apod

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Endpoints de la API APOD.
 * El parámetro api_key lo agrega automáticamente el interceptor de RetrofitInstance.
 */
interface ApodService {

    /**
     * Devuelve las imágenes del día entre dos fechas (ambas incluidas).
     *
     * @param startDate fecha inicial en formato AAAA-MM-DD.
     * @param endDate fecha final en formato AAAA-MM-DD.
     */
    @GET("planetary/apod")
    suspend fun obtenerImagenes(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): List<ApodDto>
}
