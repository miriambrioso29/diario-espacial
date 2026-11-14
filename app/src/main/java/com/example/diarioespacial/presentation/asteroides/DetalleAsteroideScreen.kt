package com.example.diarioespacial.presentation.asteroides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Detalle de un asteroide, leído desde Room por su id. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAsteroideScreen(
    id: String,
    viewModel: AsteroidesViewModel,
    onVolver: () -> Unit
) {
    // remember evita crear un Flow nuevo en cada recomposición
    val asteroide by remember(id) { viewModel.asteroide(id) }.collectAsState(initial = null)

    Scaffold(
        contentWindowInsets = SinInsets,
        topBar = {
            TopAppBar(
                title = { Text(asteroide?.name ?: "Detalle") },
                windowInsets = SinInsets,
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de favorito del detalle
                    asteroide?.let { a ->
                        BotonFavorito(esFavorito = a.isFavorite) { viewModel.alternarFavorito(a) }
                    }
                }
            )
        }
    ) { padding ->
        val a = asteroide
        if (a == null) {
            MensajeCentrado("No se encontró el asteroide.", Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(a.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                if (a.isPotentiallyHazardous) EtiquetaPeligroso()
                HorizontalDivider()
                Dato("Id", a.id)
                Dato("Fecha de aproximación", formatearFecha(a.closeApproachDate))
                Dato("Diámetro mínimo", formatearMetros(a.diameterMinMeters))
                Dato("Diámetro máximo", formatearMetros(a.diameterMaxMeters))
                Dato("Velocidad", formatearVelocidad(a.velocityKmH))
                Dato("Distancia a la Tierra", formatearDistancia(a.missDistanceKm))
                Dato("Potencialmente peligroso", if (a.isPotentiallyHazardous) "Sí" else "No")
                Dato("En mis asteroides", if (a.isFavorite) "Sí" else "No")
            }
        }
    }
}

/** Fila "etiqueta: valor" del detalle. */
@Composable
private fun Dato(etiqueta: String, valor: String) {
    Column {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
