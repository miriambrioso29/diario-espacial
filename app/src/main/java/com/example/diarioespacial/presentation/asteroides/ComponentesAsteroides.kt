package com.example.diarioespacial.presentation.asteroides

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

/*
 * Funciones de formato y componentes reutilizados por las pantallas de asteroides.
 */

/** Formato con separador de miles y como máximo 2 decimales (ej.: 12,345.67). */
private val formatoNumero = DecimalFormat("#,##0.##")

/** Formatea un número con su unidad, redondeado a máximo 2 decimales. */
fun formatearConUnidad(valor: Double, unidad: String): String =
    "${formatoNumero.format(valor)} $unidad"

/** Diámetro en metros. */
fun formatearMetros(valor: Double) = formatearConUnidad(valor, "m")

/** Velocidad en kilómetros por hora. */
fun formatearVelocidad(valor: Double) = formatearConUnidad(valor, "km/h")

/** Distancia en kilómetros. */
fun formatearDistancia(valor: Double) = formatearConUnidad(valor, "km")

/** Muestra la fecha o un texto alternativo si llegó vacía. */
fun formatearFecha(fecha: String): String = fecha.ifBlank { "Sin fecha" }

/** Sin márgenes extra: la pantalla principal ya aplica los márgenes del sistema. */
val SinInsets = WindowInsets(0, 0, 0, 0)

/** Botón de favorito: estrella llena (amarilla) si es favorito, solo contorno si no. */
@Composable
fun BotonFavorito(esFavorito: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (esFavorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
            contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
            tint = if (esFavorito) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Etiqueta roja visible para los asteroides potencialmente peligrosos. */
@Composable
fun EtiquetaPeligroso() {
    AssistChip(
        onClick = {},
        label = { Text("Potencialmente peligroso") },
        leadingIcon = { Icon(Icons.Filled.Warning, contentDescription = null) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.error,
            labelColor = MaterialTheme.colorScheme.onError,
            leadingIconContentColor = MaterialTheme.colorScheme.onError
        ),
        border = null
    )
}

/** Mensaje centrado para cuando no hay datos que mostrar. */
@Composable
fun MensajeCentrado(texto: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = texto, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
    }
}
