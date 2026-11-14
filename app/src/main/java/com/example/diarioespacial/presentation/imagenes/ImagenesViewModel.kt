package com.example.diarioespacial.presentation.imagenes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarioespacial.data.local.AppDatabase
import com.example.diarioespacial.data.local.apod.ApodEntity
import com.example.diarioespacial.data.remote.RetrofitInstance
import com.example.diarioespacial.data.remote.apod.ApodService
import com.example.diarioespacial.data.repository.ApodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * ViewModel compartido por las tres pantallas de la opción "Imágenes".
 *
 * Es un AndroidViewModel porque necesita el Context de la aplicación para
 * obtener la base de datos. Al crearse (cuando se abre la opción) consulta la API.
 */
class ImagenesViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ApodRepository(
        dao = AppDatabase.getInstance(application).apodDao(),
        servicio = RetrofitInstance.create<ApodService>()
    )

    /** Lista de imágenes guardadas en Room (fuente de verdad de la pantalla). */
    val imagenes: StateFlow<List<ApodEntity>> = repositorio.imagenes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Imágenes marcadas como favoritas. */
    val favoritas: StateFlow<List<ApodEntity>> = repositorio.favoritas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Empieza en true porque al crearse el ViewModel ya se lanza la consulta
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        refrescar()
    }

    /** Vuelve a consultar la API y actualiza Room. */
    fun refrescar() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repositorio.refrescar().onFailure { e -> _error.value = mensajeDeError(e) }
            _isLoading.value = false
        }
    }

    /** Invierte el estado de favorito de una imagen. */
    fun cambiarFavorita(imagen: ApodEntity) {
        viewModelScope.launch {
            repositorio.cambiarFavorita(imagen.date, !imagen.isFavorite)
        }
    }

    /** Observa una imagen concreta (para la pantalla de detalle). */
    fun observarImagen(date: String): Flow<ApodEntity?> = repositorio.imagen(date)

    /** Oculta el mensaje de error. */
    fun descartarError() {
        _error.value = null
    }

    /** Traduce la excepción a un mensaje comprensible para el usuario. */
    private fun mensajeDeError(e: Throwable): String = when (e) {
        is IOException -> "Sin conexión a Internet. Se muestran las imágenes guardadas."
        is HttpException -> when (e.code()) {
            401, 403 -> "La clave de la API no es válida. Revisa NASA_API_KEY en local.properties."
            429 -> "Se superó el límite de peticiones a la API. Inténtalo más tarde."
            else -> "La API de la NASA respondió con un error (${e.code()})."
        }
        else -> "No se pudieron cargar las imágenes."
    }
}
