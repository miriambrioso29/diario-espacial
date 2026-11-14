package com.example.diarioespacial.data.remote.apod

import androidx.core.text.HtmlCompat
import com.example.diarioespacial.data.local.apod.ApodEntity

/** Valores que se usan cuando la API no envía un campo. */
private const val TITULO_POR_DEFECTO = "Sin título"
private const val EXPLICACION_POR_DEFECTO = "Sin descripción disponible."
private const val TIPO_POR_DEFECTO = "image"

/** Etiqueta "Explanation:" con la que empieza la explicación en la nueva API. */
private val PREFIJO_EXPLICACION = Regex("""^\s*Explanation:\s*""", RegexOption.IGNORE_CASE)

/** Avance del día siguiente ("Tomorrow's picture: ...") que la API añade al final. */
private val AVANCE_MANANA = Regex("""\s*Tomorrow['’]s picture:.*$""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

/** Tres o más saltos de línea seguidos (quedan al convertir varios <br> o <p>). */
private val SALTOS_DE_MAS = Regex("""\n{3,}""")

/** Cualquier secuencia de espacios, tabuladores o saltos de línea. */
private val ESPACIOS = Regex("""\s+""")

/**
 * Convierte la respuesta de la API en la entidad que se guarda en Room.
 *
 * - Devuelve null si el elemento no trae fecha, porque la fecha es la clave
 *   primaria y sin ella no se puede guardar (quien llama debe descartarlo).
 * - En la nueva API "url" es la página del artículo y "hdurl" la imagen real,
 *   así que la imagen se toma de hdurl y se guarda en url y en hdurl. De este modo
 *   la lista y el detalle siempre cargan un .jpg/.png y nunca la página web.
 * - explanation, copyright y title se convierten de HTML a texto plano.
 * - El resto de campos obligatorios toman un valor por defecto si llegan nulos,
 *   para que la app nunca se cierre por datos incompletos.
 * - isFavorite no viene de la API: queda en false y el DAO conserva el valor guardado.
 */
fun ApodDto.toEntity(): ApodEntity? {
    val fecha = date?.takeIf { it.isNotBlank() } ?: return null
    val imagen = hdurl?.trim()?.takeIf { it.isNotEmpty() }
    return ApodEntity(
        date = fecha,
        title = title?.let { htmlEnUnaLinea(it) }?.takeIf { it.isNotBlank() } ?: TITULO_POR_DEFECTO,
        explanation = explanation?.let { limpiarExplicacion(it) }?.takeIf { it.isNotBlank() }
            ?: EXPLICACION_POR_DEFECTO,
        // Si no hay imagen se deja vacío: la pantalla mostrará "Imagen no disponible"
        url = imagen.orEmpty(),
        hdurl = imagen,
        mediaType = mediaType?.takeIf { it.isNotBlank() } ?: TIPO_POR_DEFECTO,
        copyright = copyright?.let { htmlEnUnaLinea(it) }?.takeIf { it.isNotEmpty() }
    )
}

/** Convierte una lista de DTO descartando los elementos sin fecha. */
fun List<ApodDto>.toEntities(): List<ApodEntity> = mapNotNull { it.toEntity() }

/** Quita las etiquetas HTML y decodifica entidades (&amp;, &#8217;...). */
private fun htmlATexto(html: String): String =
    HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

/** Texto plano en una sola línea (para título y autor). */
private fun htmlEnUnaLinea(html: String): String =
    htmlATexto(html).replace(ESPACIOS, " ").trim()

/**
 * Explicación en texto plano, sin la etiqueta inicial "Explanation:" ni el
 * avance final "Tomorrow's picture: ...", conservando los saltos de párrafo.
 */
private fun limpiarExplicacion(html: String): String =
    htmlATexto(html)
        .replace(PREFIJO_EXPLICACION, "")
        .replace(AVANCE_MANANA, "")
        .replace(SALTOS_DE_MAS, "\n\n")
        .trim()
