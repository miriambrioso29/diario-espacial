package com.example.diarioespacial.presentation.asteroides

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/** Rutas internas de la opción "Asteroides". */
private object RutasAsteroides {
    const val LISTA = "lista"
    const val DETALLE = "detalle/{id}"
    const val FAVORITOS = "favoritos"

    /** Construye la ruta del detalle para un id concreto. */
    fun detalle(id: String) = "detalle/$id"
}

/**
 * Punto de entrada de la opción "Asteroides" (NeoWs).
 * Tiene su propio NavHost con las rutas lista, detalle/{id} y favoritos.
 * El ViewModel se crea aquí para que las tres pantallas compartan el mismo estado.
 */
@Composable
fun AsteroidesNavGraph() {
    val navController = rememberNavController()
    val viewModel: AsteroidesViewModel = viewModel()

    NavHost(navController = navController, startDestination = RutasAsteroides.LISTA) {
        composable(RutasAsteroides.LISTA) {
            ListaAsteroidesScreen(
                viewModel = viewModel,
                onAsteroideClick = { id -> navController.navigate(RutasAsteroides.detalle(id)) },
                onMisAsteroidesClick = { navController.navigate(RutasAsteroides.FAVORITOS) }
            )
        }
        composable(
            route = RutasAsteroides.DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entrada ->
            DetalleAsteroideScreen(
                id = entrada.arguments?.getString("id").orEmpty(),
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }
        composable(RutasAsteroides.FAVORITOS) {
            MisAsteroidesScreen(
                viewModel = viewModel,
                onAsteroideClick = { id -> navController.navigate(RutasAsteroides.detalle(id)) },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
