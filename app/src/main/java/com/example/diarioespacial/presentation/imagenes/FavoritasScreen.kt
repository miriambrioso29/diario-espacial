package com.example.diarioespacial.presentation.imagenes

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.diarioespacial.data.local.apod.ApodEntity

/**
 * "Mis imágenes favoritas": muestra solo las marcadas, con los mismos datos
 * que la lista principal. Al pulsar el corazón se quitan de favoritos y
 * desaparecen de la lista automáticamente (Room vuelve a emitir el Flow).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritasScreen(
    viewModel: ImagenesViewModel,
    onClickImagen: (ApodEntity) -> Unit,
    onVolver: () -> Unit
) {
    val favoritas by viewModel.favoritas.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Mis imágenes favoritas") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (favoritas.isEmpty()) {
            MensajeCentrado(
                texto = "Todavía no tienes imágenes favoritas.\nPulsa el corazón de una imagen para guardarla aquí.",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            ListaImagenes(
                imagenes = favoritas,
                onClickImagen = onClickImagen,
                onClickFavorito = { viewModel.cambiarFavorita(it) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
