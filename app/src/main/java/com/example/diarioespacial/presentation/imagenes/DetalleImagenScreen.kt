package com.example.diarioespacial.presentation.imagenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.diarioespacial.data.local.apod.ApodEntity
import kotlinx.coroutines.flow.map

/** Estado de la pantalla: distingue "aún leyendo de Room" de "no existe". */
private sealed interface EstadoDetalle {
    data object Cargando : EstadoDetalle
    data class Listo(val imagen: ApodEntity?) : EstadoDetalle
}

/**
 * Detalle de una imagen: imagen grande, título, fecha, explicación completa,
 * autor (solo si existe) y botón de favorito.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleImagenScreen(
    date: String,
    viewModel: ImagenesViewModel,
    onVolver: () -> Unit
) {
    // Se recuerda el Flow por fecha para no volver a crearlo en cada recomposición
    val estado by remember(date) {
        viewModel.observarImagen(date).map { EstadoDetalle.Listo(it) }
    }.collectAsState(initial = EstadoDetalle.Cargando)

    val imagen = (estado as? EstadoDetalle.Listo)?.imagen

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (imagen != null) {
                        BotonFavorito(
                            esFavorita = imagen.isFavorite,
                            onClick = { viewModel.cambiarFavorita(imagen) }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                estado is EstadoDetalle.Cargando ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                imagen == null ->
                    MensajeCentrado(texto = "No se encontró la imagen del $date.")
                else ->
                    ContenidoDetalle(imagen = imagen, onClickFavorito = { viewModel.cambiarFavorita(imagen) })
            }
        }
    }
}

@Composable
private fun ContenidoDetalle(imagen: ApodEntity, onClickFavorito: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (imagen.esVideo) {
            AvisoSinImagen(
                texto = "Contenido en video",
                esVideo = true,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        } else {
            // Se prefiere la versión en alta resolución si la API la envía
            ImagenApod(
                url = imagen.hdurl ?: imagen.url,
                descripcion = imagen.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp)
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = imagen.title, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = imagen.date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BotonFavorito(esFavorita = imagen.isFavorite, onClick = onClickFavorito)
            }

            // El autor solo se muestra si la API lo envió
            imagen.copyright?.let { autor ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Autor: $autor",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(text = imagen.explanation, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
