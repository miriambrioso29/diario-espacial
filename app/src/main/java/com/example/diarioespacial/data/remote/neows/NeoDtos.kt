package com.example.diarioespacial.data.remote.neows

import com.google.gson.annotations.SerializedName

/*
 * DTOs (objetos de transferencia) que representan la respuesta JSON del
 * endpoint "feed" de NeoWs. TODOS los campos son nullable para que Gson
 * no falle si la API omite algún dato; los valores por defecto se aplican
 * después, en la conversión a AsteroidEntity (ver NeoMapper.kt).
 */

/** Respuesta completa de GET neo/rest/v1/feed. */
data class NeoFeedResponse(
    @SerializedName("element_count") val elementCount: Int? = null,
    // Objeto cuyas claves son fechas (AAAA-MM-DD) y cuyos valores son listas de asteroides
    @SerializedName("near_earth_objects") val nearEarthObjects: Map<String, List<NeoDto>>? = null
)

/** Un asteroide cercano a la Tierra. */
data class NeoDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("is_potentially_hazardous_asteroid") val isPotentiallyHazardous: Boolean? = null,
    @SerializedName("estimated_diameter") val estimatedDiameter: EstimatedDiameterDto? = null,
    @SerializedName("close_approach_data") val closeApproachData: List<CloseApproachDto>? = null
)

/** Diámetro estimado en distintas unidades; solo usamos metros. */
data class EstimatedDiameterDto(
    @SerializedName("meters") val meters: DiameterRangeDto? = null
)

/** Rango de diámetro estimado (mínimo y máximo). */
data class DiameterRangeDto(
    @SerializedName("estimated_diameter_min") val min: Double? = null,
    @SerializedName("estimated_diameter_max") val max: Double? = null
)

/** Datos de una aproximación a la Tierra. */
data class CloseApproachDto(
    @SerializedName("close_approach_date") val closeApproachDate: String? = null,
    @SerializedName("relative_velocity") val relativeVelocity: RelativeVelocityDto? = null,
    @SerializedName("miss_distance") val missDistance: MissDistanceDto? = null
)

/** Velocidad relativa. La API la envía como TEXTO (por ejemplo "45123.456"). */
data class RelativeVelocityDto(
    @SerializedName("kilometers_per_hour") val kilometersPerHour: String? = null
)

/** Distancia mínima a la Tierra. La API la envía como TEXTO. */
data class MissDistanceDto(
    @SerializedName("kilometers") val kilometers: String? = null
)
