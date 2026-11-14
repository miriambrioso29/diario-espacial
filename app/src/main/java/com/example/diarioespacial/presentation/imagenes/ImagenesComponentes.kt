package com.example.diarioespacial.presentation.imagenes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.diarioespacial.data.local.apod.ApodEntity

/** Valor de media_type que indica que el APOD del día es un video. */
const val TIPO_VIDEO = "video"

/** Indica si la imagen es en realidad un video (no se carga con Coil). */
val ApodEntity.esVideo: Boolean get() = mediaType == TIPO_VIDEO

/**
 * Lista de imágenes reutilizada por la pantalla principal y la de favoritos.
 */
@Composable
fun ListaImagenes(
    imagenes: List<ApodEntity>,
    onClickImagen: (ApodEntity) -> Unit,
    onClickFavorito: (ApodEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // La fecha es única, así que sirve como clave estable de cada elemento
        items(imagenes, key = { it.date }) { imagen ->
            TarjetaImagen(
                imagen = imagen,
                onClick = { onClickImagen(imagen) },
                onClickFavorito = { onClickFavorito(imagen) }
            )
        }
    }
}

/**
 * Elemento de la lista: miniatura (o aviso de video), título, fecha y botón de favorito.
 */
@Composable
fun TarjetaImagen(
    imagen: ApodEntity,
    onClick: () -> Unit,
    onClickFavorito: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            if (imagen.esVideo) {
                AvisoSinImagen(
                    texto = "Contenido en video",
                    esVideo = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            } else {
                ImagenApod(
                    url = imagen.url,
                    descripcion = imagen.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = imagen.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = imagen.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BotonFavorito(esFavorita = imagen.isFavorite, onClick = onClickFavorito)
            }
        }
    }
}

/**
 * Carga una imagen con Coil. Si la URL está vacía (la API no la envió)
 * muestra un aviso en su lugar.
 */
@Composable
fun ImagenApod(
    url: String,
    descripcion: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (url.isBlank()) {
        AvisoSinImagen(texto = "Imagen no disponible", esVideo = false, modifier = modifier)
    } else {
        AsyncImage(
            model = url,
            contentDescription = descripcion,
            contentScale = contentScale,
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}

/** Recuadro con icono y texto que sustituye a la imagen (videos o imágenes sin URL). */
@Composable
fun AvisoSinImagen(texto: String, esVideo: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (esVideo) Icons.Filled.PlayCircle else Icons.Filled.ImageNotSupported,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Botón de corazón: lleno si es favorita, vacío si no. */
@Composable
fun BotonFavorito(esFavorita: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = if (esFavorita) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (esFavorita) "Quitar de favoritos" else "Agregar a favoritos",
            tint = if (esFavorita) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Mensaje centrado en pantalla (lista vacía, sin favoritos, error...). */
@Composable
fun MensajeCentrado(texto: String, modifier: Modifier = Modifier, accion: @Composable (() -> Unit)? = null) {
    Box(modifier = modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            if (accion != null) {
                Spacer(Modifier.height(16.dp))
                accion()
            }
        }
    }
}
