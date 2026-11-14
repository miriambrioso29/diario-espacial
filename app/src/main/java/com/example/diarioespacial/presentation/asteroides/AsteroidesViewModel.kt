package com.example.diarioespacial.presentation.asteroides

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarioespacial.data.local.AppDatabase
import com.example.diarioespacial.data.local.asteroid.AsteroidEntity
import com.example.diarioespacial.data.remote.RetrofitInstance
import com.example.diarioespacial.data.remote.neows.NeoWsService
import com.example.diarioespacial.data.repository.AsteroidRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de la opción "Asteroides". Lo comparten las tres pantallas
 * (lista, detalle y "Mis asteroides").
 *
 * Expone el estado como StateFlow: lista, filtro, carga y error.
 * Al crearse (al abrir la opción) consulta la API.
 */
class AsteroidesViewModel(application: Application) : AndroidViewModel(application) {

    // Se construye el repositorio con la base de datos y el Retrofit compartidos
    private val repositorio = AsteroidRepository(
        dao = AppDatabase.getInstance(application).asteroidDao(),
        api = RetrofitInstance.create<NeoWsService>()
    )

    /** Estado del Switch "Solo potencialmente peligrosos". */
    private val _soloPeligrosos = MutableStateFlow(false)
    val soloPeligrosos: StateFlow<Boolean> = _soloPeligrosos.asStateFlow()

    /** true mientras se consulta la API. */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Mensaje de error de la última consulta (null si no hubo error). */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Lista que muestra la pantalla principal. Según el filtro se observa
     * la consulta de todos o la de solo peligrosos (siempre desde Room).
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val asteroides: StateFlow<List<AsteroidEntity>> = _soloPeligrosos
        .flatMapLatest { solo -> if (solo) repositorio.peligrosos else repositorio.asteroides }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Asteroides marcados como favoritos. */
    val favoritos: StateFlow<List<AsteroidEntity>> = repositorio.favoritos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Consulta la API al abrir la opción
        refrescar()
    }

    /** Consulta la API y guarda en Room. Si falla, se muestran los datos guardados. */
    fun refrescar() {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repositorio.refrescar().onFailure { e ->
                _error.value = "No se pudo consultar la API (${e.message ?: "error desconocido"}). " +
                    "Se muestran los datos guardados."
            }
            _isLoading.value = false
        }
    }

    /** Cambia el filtro de solo potencialmente peligrosos. */
    fun cambiarFiltro(soloPeligrosos: Boolean) {
        _soloPeligrosos.value = soloPeligrosos
    }

    /** Invierte el estado de favorito de un asteroide. */
    fun alternarFavorito(asteroide: AsteroidEntity) {
        viewModelScope.launch {
            repositorio.cambiarFavorito(asteroide.id, !asteroide.isFavorite)
        }
    }

    /** Quita un asteroide de "Mis asteroides". */
    fun quitarFavorito(id: String) {
        viewModelScope.launch { repositorio.cambiarFavorito(id, false) }
    }

    /** Observa un asteroide por id (para la pantalla de detalle). */
    fun asteroide(id: String): Flow<AsteroidEntity?> = repositorio.asteroide(id)
}
