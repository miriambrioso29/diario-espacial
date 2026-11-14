package com.example.diarioespacial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.diarioespacial.presentation.asteroides.AsteroidesNavGraph
import com.example.diarioespacial.presentation.imagenes.ImagenesNavGraph
import com.example.diarioespacial.ui.theme.DiarioEspacialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiarioEspacialTheme {
                PantallaPrincipal()
            }
        }
    }
}

/** Opciones de la barra de navegación inferior. */
private enum class OpcionPrincipal(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
) {
    IMAGENES("imagenes", "Imágenes", Icons.Filled.Image),
    ASTEROIDES("asteroides", "Asteroides", Icons.Filled.Public)
}

/**
 * Pantalla principal: Scaffold con barra de navegación inferior y un NavHost
 * con una ruta por opción. Cada ruta delega en el NavGraph de su alumno.
 */
@Composable
fun PantallaPrincipal() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val destinoActual = entradaActual?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                OpcionPrincipal.entries.forEach { opcion ->
                    NavigationBarItem(
                        selected = destinoActual?.hierarchy?.any { it.route == opcion.ruta } == true,
                        onClick = {
                            navController.navigate(opcion.ruta) {
                                // Evita acumular pantallas al cambiar de pestaña
                                // y conserva el estado de cada opción.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(opcion.icono, contentDescription = opcion.etiqueta) },
                        label = { Text(opcion.etiqueta) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = OpcionPrincipal.IMAGENES.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(OpcionPrincipal.IMAGENES.ruta) { ImagenesNavGraph() }
            composable(OpcionPrincipal.ASTEROIDES.ruta) { AsteroidesNavGraph() }
        }
    }
}
