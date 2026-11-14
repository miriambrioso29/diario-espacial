package com.example.diarioespacial.presentation.asteroides

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diarioespacial.data.local.asteroid.AsteroidEntity

/**
 * Pantalla principal de asteroides: lista de los últimos 7 días con
 * filtro de peligrosos, favoritos y acceso a "Mis asteroides".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaAsteroidesScreen(
    viewModel: AsteroidesViewModel,
    onAsteroideClick: (String) -> Unit,
    onMisAsteroidesClick: () -> Unit
) {
    val asteroides by viewModel.asteroides.collectAsState()
    val soloPeligrosos by viewModel.soloPeligrosos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        contentWindowInsets = SinInsets,
        topBar = {
            TopAppBar(
                title = { Text("Asteroides") },
                windowInsets = SinInsets,
                actions = {
                    IconButton(onClick = viewModel::refrescar, enabled = !isLoading) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                    // Botón que abre la pantalla "Mis asteroides"
                    TextButton(onClick = onMisAsteroidesClick) {
                        Icon(Icons.Filled.Star, contentDescription = null)
                        Spacer(Modifier.size(4.dp))
                        Text("Mis asteroides")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Filtro: solo potencialmente peligrosos
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Solo potencialmente peligrosos", modifier = Modifier.weight(1f))
                Switch(checked = soloPeligrosos, onCheckedChange = viewModel::cambiarFiltro)
            }

            // Barra de carga fina cuando ya hay datos y se está actualizando
            if (isLoading && asteroides.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Mensaje de error (los datos guardados se siguen mostrando debajo)
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            when {
                // Primera carga sin datos guardados: indicador centrado
                isLoading && asteroides.isEmpty() -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.size(8.dp))
                    Text("Consultando asteroides…")
                }

                asteroides.isEmpty() -> MensajeCentrado(
                    if (soloPeligrosos) "No hay asteroides potencialmente peligrosos guardados."
                    else "No hay asteroides para mostrar."
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(asteroides, key = { it.id }) { asteroide ->
                        AsteroideItem(
                            asteroide = asteroide,
                            onClick = { onAsteroideClick(asteroide.id) },
                            onFavoritoClick = { viewModel.alternarFavorito(asteroide) }
                        )
                    }
                }
            }
        }
    }
}

/** Tarjeta de un asteroide en la lista. Los peligrosos tienen fondo rojizo y etiqueta. */
@Composable
private fun AsteroideItem(
    asteroide: AsteroidEntity,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    val colores = if (asteroide.isPotentiallyHazardous) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    } else {
        CardDefaults.cardColors()
    }

    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), colors = colores) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = asteroide.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Aproximación: ${formatearFecha(asteroide.closeApproachDate)}")
                Text("Diámetro máx.: ${formatearMetros(asteroide.diameterMaxMeters)}")
                Text("Velocidad: ${formatearVelocidad(asteroide.velocityKmH)}")
                Text("Distancia: ${formatearDistancia(asteroide.missDistanceKm)}")
                if (asteroide.isPotentiallyHazardous) {
                    EtiquetaPeligroso()
                }
            }
            BotonFavorito(esFavorito = asteroide.isFavorite, onClick = onFavoritoClick)
        }
    }
}
