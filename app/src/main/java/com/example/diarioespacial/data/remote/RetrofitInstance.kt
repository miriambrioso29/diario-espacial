package com.example.diarioespacial.data.remote

import com.example.diarioespacial.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Instancia única de Retrofit compartida por toda la app.
 *
 * Aquí NO se declaran endpoints: cada alumno crea su propia interfaz
 * (ApodService / NeoWsService) en su rama y la obtiene con [create], por ejemplo:
 *
 *     val api = RetrofitInstance.create<ApodService>()
 */
object RetrofitInstance {

    /** URL base común de las APIs de la NASA. */
    private const val BASE_URL = "https://api.nasa.gov/"

    /** Agrega el parámetro api_key a todas las peticiones. */
    private val apiKeyInterceptor = Interceptor { chain ->
        val peticionOriginal = chain.request()
        val urlConClave = peticionOriginal.url.newBuilder()
            .addQueryParameter("api_key", BuildConfig.NASA_API_KEY)
            .build()
        chain.proceed(peticionOriginal.newBuilder().url(urlConClave).build())
    }

    /** Muestra las peticiones en Logcat solo en compilaciones de depuración. */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        // Evita que la clave se imprima en los logs
        redactQueryParams("api_key")
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    /** Retrofit configurado con la URL base, el cliente OkHttp y el conversor Gson. */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Crea una implementación de cualquier interfaz de servicio de Retrofit. */
    fun <T> create(service: Class<T>): T = retrofit.create(service)

    /** Versión genérica con tipo reificado: RetrofitInstance.create<MiServicio>() */
    inline fun <reified T> create(): T = create(T::class.java)
}
