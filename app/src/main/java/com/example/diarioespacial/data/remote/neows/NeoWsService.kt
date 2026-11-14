package com.example.diarioespacial.data.remote.neows

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Endpoints de la API NeoWs (Near Earth Object Web Service) de la NASA.
 * El parámetro api_key NO se declara aquí: lo agrega automáticamente
 * el interceptor de RetrofitInstance.
 *
 * Uso: val api = RetrofitInstance.create<NeoWsService>()
 */
interface NeoWsService {

    /**
     * Asteroides que se aproximan a la Tierra entre dos fechas.
     *
     * @param startDate fecha inicial en formato AAAA-MM-DD.
     * @param endDate fecha final en formato AAAA-MM-DD (máximo 7 días después de startDate).
     */
    @GET("neo/rest/v1/feed")
    suspend fun getFeed(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): NeoFeedResponse
}
