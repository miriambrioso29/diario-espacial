package com.example.diarioespacial.presentation.imagenes

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/** Rutas internas de la opción "Imágenes". */
private object RutasImagenes {
    const val LISTA = "lista"
    const val FAVORITOS = "favoritos"
    const val ARG_FECHA = "date"
    const val DETALLE = "detalle/{$ARG_FECHA}"

    fun detalle(date: String) = "detalle/$date"
}

/**
 * Punto de entrada de la opción "Imágenes" (APOD).
 *
 * Tiene su propio NavHost con las rutas lista, detalle/{date} y favoritos.
 * El ViewModel se crea aquí una sola vez y se comparte entre las tres
 * pantallas, así la API se consulta al abrir la opción y no en cada pantalla.
 */
@Composable
fun ImagenesNavGraph() {
    val navController = rememberNavController()
    val viewModel: ImagenesViewModel = viewModel()

    NavHost(navController = navController, startDestination = RutasImagenes.LISTA) {

        composable(RutasImagenes.LISTA) {
            ListaImagenesScreen(
                viewModel = viewModel,
                onClickImagen = { navController.navigate(RutasImagenes.detalle(it.date)) },
                onClickFavoritas = { navController.navigate(RutasImagenes.FAVORITOS) }
            )
        }

        composable(
            route = RutasImagenes.DETALLE,
            arguments = listOf(navArgument(RutasImagenes.ARG_FECHA) { type = NavType.StringType })
        ) { entrada ->
            val date = entrada.arguments?.getString(RutasImagenes.ARG_FECHA).orEmpty()
            DetalleImagenScreen(
                date = date,
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(RutasImagenes.FAVORITOS) {
            FavoritasScreen(
                viewModel = viewModel,
                onClickImagen = { navController.navigate(RutasImagenes.detalle(it.date)) },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
