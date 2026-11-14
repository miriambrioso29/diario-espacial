import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // KSP: necesario para el compilador de Room
    alias(libs.plugins.ksp)
}

// Lee la clave de la API de la NASA desde local.properties (archivo NO versionado).
// Si no está definida, queda como cadena vacía (las peticiones a la API fallarán).
val localProperties = Properties().apply {
    val archivo = rootProject.file("local.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}
val nasaApiKey: String = localProperties.getProperty("NASA_API_KEY") ?: ""

android {
    namespace = "com.example.diarioespacial"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.diarioespacial"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Expone la clave como BuildConfig.NASA_API_KEY
        buildConfigField("String", "NASA_API_KEY", "\"$nasaApiKey\"")
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        // Necesario para generar la clase BuildConfig
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Red: Retrofit + Gson + logs de OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    // Base de datos local: Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Navegación, carga de imágenes, ViewModel e iconos
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.material.icons.extended)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}