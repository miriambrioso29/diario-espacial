package com.example.diarioespacial.presentation.imagenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diarioespacial.data.local.apod.ApodEntity

/**
 * Pantalla principal de la opción "Imágenes": lista de los últimos 7 días.
 * Los datos salen siempre de Room (a través del ViewModel).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaImagenesScreen(
    viewModel: ImagenesViewModel,
    onClickImagen: (ApodEntity) -> Unit,
    onClickFavoritas: () -> Unit
) {
    val imagenes by viewModel.imagenes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        // El Scaffold de MainActivity ya deja el margen de la barra de estado
        // y de la barra inferior, así que aquí no se añaden insets otra vez.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Imagen astronómica del día") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                actions = {
                    IconButton(onClick = { viewModel.refrescar() }, enabled = !isLoading) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                    IconButton(onClick = onClickFavoritas) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Mis imágenes favoritas")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                // Primera carga sin datos guardados: indicador centrado
                isLoading && imagenes.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // Sin datos: mensaje en lugar de una lista vacía
                imagenes.isEmpty() -> {
                    MensajeCentrado(
                        texto = error ?: "No hay imágenes para mostrar.",
                        accion = {
                            Button(onClick = { viewModel.refrescar() }) { Text("Reintentar") }
                        }
                    )
                }
                // Hay datos: se muestran aunque la API haya fallado
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (isLoading) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        error?.let { mensaje ->
                            AvisoError(mensaje = mensaje, onDescartar = { viewModel.descartarError() })
                        }
                        ListaImagenes(
                            imagenes = imagenes,
                            onClickImagen = onClickImagen,
                            onClickFavorito = { viewModel.cambiarFavorita(it) }
                        )
                    }
                }
            }
        }
    }
}

/** Franja con el mensaje de error que aparece encima de los datos guardados. */
@Composable
private fun AvisoError(mensaje: String, onDescartar: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 8.dp)) {
            Text(text = mensaje, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onDescartar, modifier = Modifier.align(Alignment.End)) {
                Text("Cerrar")
            }
        }
    }
}
