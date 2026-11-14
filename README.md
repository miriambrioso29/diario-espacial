# Diario espacial

App Android hecha con Kotlin y Jetpack Compose. Consume dos servicios de la NASA, guarda la información en una base de datos local (Room) y permite marcar favoritos. Las pantallas siempre leen los datos desde Room, así que si no hay conexión o la API falla se siguen mostrando los datos guardados.

## Integrantes

| Alumno | Nombre | Opción |
|---|---|---|
| Alumno 1 | Miriam Brioso | Imágenes (APOD) |
| Alumno 2 | Mijhail Medina | Asteroides (NeoWs) |

## Descripción de la app

La pantalla principal tiene una barra de navegación inferior con dos opciones. Cada una tiene su propia navegación interna con tres pantallas: lista, detalle y favoritos.

### Imágenes (APOD) – Alumno 1

Muestra la *Astronomy Picture of the Day* (imagen astronómica del día) de los últimos 7 días.

- **Lista ("Imagen astronómica del día"):** cada elemento muestra la imagen, el título, la fecha y un botón de favorito (corazón). Tiene indicador de carga, mensaje de error y botón "Reintentar".
- **Detalle:** imagen grande, título, fecha, autor (si lo hay), explicación completa y botón de favorito. Si el contenido del día es un video, se indica como "Contenido en video".
- **Favoritos ("Mis imágenes favoritas"):** solo las imágenes marcadas; permite quitarlas.

### Asteroides (NeoWs) – Alumno 2

Muestra los asteroides que se aproximan a la Tierra en los últimos 7 días (de hoy menos 6 días hasta hoy).

- **Lista ("Asteroides"):**
  - Switch **"Solo potencialmente peligrosos"** que filtra la lista.
  - Cada elemento muestra el nombre, la fecha de aproximación, el diámetro máximo (m), la velocidad (km/h) y la distancia a la Tierra (km), redondeados a máximo 2 decimales, y un botón de favorito (estrella).
  - Los asteroides potencialmente peligrosos tienen fondo rojizo y la etiqueta roja "Potencialmente peligroso".
  - En la barra superior hay un botón para actualizar y otro que abre "Mis asteroides".
  - Indicador de carga mientras se consulta la API y mensaje si no hay datos.
- **Detalle:** nombre, id, fecha de aproximación, diámetro mínimo y máximo, velocidad, distancia, si es potencialmente peligroso y botón de favorito.
- **Mis asteroides:** solo los asteroides marcados; permite quitarlos y muestra un mensaje si no hay ninguno.

Al refrescar desde la API, los favoritos ya guardados **no se pierden**.

## Tecnologías

- **Kotlin**
- **Jetpack Compose** (Material 3) para la interfaz.
- **Navigation Compose**: navegación principal con barra inferior y un `NavHost` interno por opción.
- **Retrofit + Gson** (con OkHttp) para consumir las APIs de la NASA.
- **Room** (con KSP) como base de datos local.
- **Coil** para cargar las imágenes de APOD.
- **ViewModel con StateFlow** para el estado de cada pantalla.
- **Patrón Repository**: el repositorio consulta la API, guarda en Room y expone los datos como `Flow`.

## Cómo ejecutar el proyecto

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/miriambrioso29/diario-espacial.git
   ```

2. Abrir la carpeta del proyecto en **Android Studio**.
3. Generar una clave gratuita en <https://api.nasa.gov> (formulario **Generate API Key**). La clave llega al correo.
4. Agregar esta línea al archivo `local.properties` de la **raíz** del proyecto:

   ```properties
   NASA_API_KEY=tu_clave
   ```

   `local.properties` está en `.gitignore`, así que **la clave nunca se sube al repositorio**. Gradle la lee y la expone como `BuildConfig.NASA_API_KEY`, y `RetrofitInstance` la añade automáticamente a todas las peticiones. Si no se define, el proyecto compila igual, pero las peticiones a la API fallarán.

5. Sincronizar Gradle (**Sync Project with Gradle Files**).
6. Ejecutar la app en un emulador o dispositivo (botón **Run**).

## Estructura del proyecto

Código en `app/src/main/java/com/example/diarioespacial/`:

```
diarioespacial/
├── MainActivity.kt                 Base común
├── ui/theme/                       Base común
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt          Base común
│   │   ├── apod/                   Alumno 1
│   │   └── asteroid/               Alumno 2  (AsteroidEntity lo creó la base común)
│   ├── remote/
│   │   ├── RetrofitInstance.kt     Base común
│   │   ├── apod/                   Alumno 1
│   │   └── neows/                  Alumno 2
│   └── repository/
│       ├── ApodRepository.kt       Alumno 1
│       └── AsteroidRepository.kt   Alumno 2
└── presentation/
    ├── imagenes/                   Alumno 1
    └── asteroides/                 Alumno 2
```

| Parte | Archivos |
|---|---|
| **Base común** | `MainActivity` (barra inferior y NavHost principal), `ui/theme/`, `AppDatabase` (tablas `apod` y `asteroids`), `RetrofitInstance` (URL base e interceptor de `api_key`), entidades iniciales y dependencias en `gradle/libs.versions.toml` |
| **Alumno 1 – Imágenes** | `data/local/apod/` (`ApodDao`, `ApodEntity`), `data/remote/apod/` (`ApodService`, `ApodDto`, `ApodMapper`), `data/repository/ApodRepository.kt`, `presentation/imagenes/` |
| **Alumno 2 – Asteroides** | `data/local/asteroid/` (`AsteroidDao`, `AsteroidEntity`), `data/remote/neows/` (`NeoWsService`, `NeoDtos`, `NeoMapper`), `data/repository/AsteroidRepository.kt`, `presentation/asteroides/` |

## Acuerdo de coordinación

1. **Base común.** Miriam preparó la base común en la rama `base-comun`:
   1. Dependencias en el version catalog (Retrofit, OkHttp, Room con KSP, Navigation Compose, Coil, ViewModel, iconos) y permiso `INTERNET`.
   2. Lectura de `NASA_API_KEY` desde `local.properties` y exposición como `BuildConfig.NASA_API_KEY`.
   3. Retrofit compartido en `data/remote/RetrofitInstance.kt` (sin endpoints).
   4. Room compartido en `data/local`: `AppDatabase` con las tablas `apod` y `asteroids` ya declaradas y sus DAO vacíos.
   5. Navegación principal: barra inferior con "Imágenes" y "Asteroides", y un NavHost que delega en `ImagenesNavGraph` y `AsteroidesNavGraph`.
2. **Revisión.** Mijhail revisa y aprueba la base común en un Pull Request de `base-comun` hacia `main`.
3. **Trabajo individual.** Una vez fusionado el PR, cada uno crea su rama desde `main`:
   - `alumno1-imagenes` (Miriam)
   - `alumno2-asteroides` (Mijhail)
4. **Sin conflictos.** Cada alumno solo modifica los archivos de su opción:

   | Alumno 1 – Imágenes | Alumno 2 – Asteroides |
   |---|---|
   | `data/local/apod/` (`ApodDao`, …) | `data/local/asteroid/` (`AsteroidDao`, …) |
   | `data/remote/` → su interfaz `ApodService` | `data/remote/` → su interfaz `NeoWsService` |
   | `presentation/imagenes/` | `presentation/asteroides/` |

   Los archivos comunes (`AppDatabase`, `RetrofitInstance`, `MainActivity`, dependencias) no se modifican en las ramas individuales; si hace falta un cambio común, se acuerda entre los dos y se hace en un PR aparte.

### Cómo trabajamos

Nadie trabajó directamente sobre `main`. Cada parte se hizo en su propia rama, creada desde `main`, y se integró con un Pull Request:

| Rama | Responsable | Contenido |
|---|---|---|
| `base-comun` | Miriam | Dependencias, clave de la API, Retrofit, Room y navegación principal |
| `alumno1-imagenes` | Miriam | Opción Imágenes (APOD): lista, detalle y favoritos |
| `alumno2-asteroides` | Mijhail | Opción Asteroides (NeoWs): lista con filtro, detalle y "Mis asteroides" |
| `alumno1-apod-nueva-api` | Miriam | Migración de APOD a la nueva API de science.nasa.gov |
| `docs-readme` | Mijhail | Este README |

Cada Pull Request fue revisado y aprobado por el otro integrante antes de fusionarse a `main`. Hicimos commits pequeños con mensajes en español y comentamos el código en español.

## Nota sobre APOD

La NASA migró el servicio APOD. La dirección antigua (`https://api.nasa.gov/planetary/apod`) devolvía contenido genérico y **se apaga el 1 de diciembre de 2026**. Por eso, según la documentación de [api.nasa.gov](https://api.nasa.gov), la opción Imágenes usa la nueva dirección:

```
https://science.nasa.gov/wp-json/wp/v2/apod-basic
```

Cambios que implicó la nueva API (ver `data/remote/apod/`):

- Como el dominio es distinto de la URL base de Retrofit (`https://api.nasa.gov/`), `ApodService` usa la URL completa en `@GET`. El `api_key` lo sigue agregando el interceptor.
- En la nueva API, `url` es la página del artículo y `hdurl` es la imagen real, así que la imagen se toma de `hdurl`.
- `title`, `explanation` y `copyright` llegan con HTML y se convierten a texto plano.
- La nueva API ignora `start_date` y `end_date` y devuelve las últimas publicaciones, así que el repositorio filtra por su cuenta el rango de los últimos 7 días.
