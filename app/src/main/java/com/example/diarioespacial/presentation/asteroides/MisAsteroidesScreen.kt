package com.example.diarioespacial.presentation.asteroides

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Pantalla "Mis asteroides": solo los marcados como favoritos, con opción de quitarlos. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisAsteroidesScreen(
    viewModel: AsteroidesViewModel,
    onAsteroideClick: (String) -> Unit,
    onVolver: () -> Unit
) {
    val favoritos by viewModel.favoritos.collectAsState()

    Scaffold(
        contentWindowInsets = SinInsets,
        topBar = {
            TopAppBar(
                title = { Text("Mis asteroides") },
                windowInsets = SinInsets,
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (favoritos.isEmpty()) {
            MensajeCentrado(
                "Aún no tienes asteroides guardados.\nMárcalos con la estrella en la lista.",
                Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoritos, key = { it.id }) { a ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { onAsteroideClick(a.id) }) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(a.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Aproximación: ${formatearFecha(a.closeApproachDate)}")
                                Text("Diámetro máx.: ${formatearMetros(a.diameterMaxMeters)}")
                                if (a.isPotentiallyHazardous) EtiquetaPeligroso()
                            }
                            // Quita el asteroide de "Mis asteroides"
                            IconButton(onClick = { viewModel.quitarFavorito(a.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Quitar de mis asteroides")
                            }
                        }
                    }
                }
            }
        }
    }
}
