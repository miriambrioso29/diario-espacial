# Diario espacial

App Android (Kotlin + Jetpack Compose) que consume la API de la NASA y guarda la información en una base de datos local con Room.

## Integrantes

| Alumno | Nombre | Opción |
|---|---|---|
| Alumno 1 | Miriam Brioso | Imágenes (APOD) |
| Alumno 2 | Mijhail | Asteroides (NeoWs) |

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

## Configurar la clave de la API

La clave de la NASA **no se sube al repositorio**. Cada integrante debe:

1. Obtener una clave gratuita en <https://api.nasa.gov/>.
2. Añadir esta línea al archivo `local.properties` de la raíz del proyecto (ya está en `.gitignore`):

   ```properties
   NASA_API_KEY=tu_clave_aqui
   ```

3. Sincronizar Gradle. La clave queda disponible en el código como `BuildConfig.NASA_API_KEY`, y `RetrofitInstance` la añade automáticamente a todas las peticiones.

Si no se define, se usa `DEMO_KEY`, que funciona pero con un límite de peticiones muy bajo.
