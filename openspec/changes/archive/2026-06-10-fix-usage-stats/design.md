# Design: Fix Usage Stats — Precisión de datos de uso

## Enfoque técnico

Reemplazar `queryEvents()` con `queryAndAggregateUsageStats()` para medir tiempo real en foreground de apps. Mantener `queryEvents()` solo para conteo de desbloqueos vía `KEYGUARD_HIDDEN`. Eliminar reglas de inflación artificial (`1h` si <1h, `1 unlock` si 0). Introducir `TodayUsageStats` como modelo tipado con `formattedTime`.

## Decisiones de arquitectura

| Decisión | Opciones | Trade-off | Decisión |
|---|---|---|---|
| API para foreground time | A: queryEvents + SCREEN_INTERACTIVE B: queryAndAggregateUsageStats | A mide estado físico de pantalla, no foreground real de apps. B devuelve ms agregados por paquete, exacto para nuestro caso | **B** — `getTotalTimeInForeground()` suma por paquete |
| Conteo de desbloqueos | A: KEYGUARD_HIDDEN B: ACTION_USER_UNLOCKED (BroadcastReceiver) | queryEvents es la API directa, no requiere registro adicional. No hay agregación para este evento | **A** — Mantener KEYGUARD_HIDDEN |
| Modelo de datos | A: Pair<Int,Int> B: TodayUsageStats | Pair no es extensible y mezcla significado. Data class permite computed properties y nombre explícito | **B** — `TodayUsageStats(totalForegroundMs, unlockCount)` |
| Testeabilidad | A: Robolectric shadow B: Interface + fake | Robolectric provee fake de UsageStatsManager. Interface es más explícito para tests unitarios puros | **B** — `UsageStatsDataSource` interface + impl real |


## Flujo de datos

```
queryAndAggregateUsageStats(start, end)
    │
    └─ sum(getTotalTimeInForeground()) → totalForegroundMs
                                          │
queryEvents(start, end)                   │
    │                                     │
    └─ KEYGUARD_HIDDEN count → unlockCount│
                                      │   │
                                      ▼   ▼
                              TodayUsageStats
                                      │
                              OnboardingViewModel
                              _realUsageStats: StateFlow<TodayUsageStats>
                                      │
                              RealityRevealScreen
                              formattedTime → "3h 42m"
                              unlocks → "47"
```

## Cambios en archivos

| Archivo | Acción | Descripción |
|---|---|---|
| `core/model/TodayUsageStats.kt` | Crear | Data class con `totalForegroundMs`, `unlockCount`, computed `formattedTime` |
| `core/repository/UsageStatsDataSource.kt` | Crear | Interface + impl real para testear repository |
| `core/di/SecurityModule.kt` | Modificar | Agregar provider de `UsageStatsDataSource` (real) |
| `core/repository/AppUsageRepository.kt` | Modificar | Rewrite `getTodayUsageStats()` con nueva API |
| `ui/…/onboarding/OnboardingViewModel.kt` | Modificar | State: `StateFlow<TodayUsageStats>` en vez de dos `StateFlow<Int>` |
| `ui/…/onboarding/RealityRevealScreen.kt` | Modificar | Parámetro `TodayUsageStats` en vez de `Int hours` |

## Cambios de código

### TodayUsageStats.kt (nuevo)

```kotlin
package com.protas.enfocaapp.core.model

data class TodayUsageStats(
    val totalForegroundMs: Long,
    val unlockCount: Int
) {
    val hours: Int get() = (totalForegroundMs / (1000 * 60 * 60)).toInt()
    val minutes: Int get() = ((totalForegroundMs % (1000 * 60 * 60)) / (1000 * 60)).toInt()
    val formattedTime: String get() = "${hours}h ${minutes}m"
}
```

### AppUsageRepository.getTodayUsageStats()

Antes — `queryEvents` mide estado de pantalla + inflación:
```kotlin
fun getTodayUsageStats(): Pair<Int, Int> {
    val events = usageStatsManager.queryEvents(startTime, endTime)
    // SCREEN_INTERACTIVE / SCREEN_NON_INTERACTIVE loop
    val totalHours = (totalInteractiveTimeMs / ...).toInt()
    val displayHours = if (totalHours == 0 && totalInteractiveTimeMs > 0) 1 else totalHours
    val displayUnlocks = if (unlocks == 0) 1 else unlocks
    return Pair(displayHours, displayUnlocks)
}
```

Después — `queryAndAggregateUsageStats` + sin inflación:
```kotlin
fun getTodayUsageStats(): TodayUsageStats {
    if (!hasUsageStatsPermission()) return TodayUsageStats(0, 0)
    val appUsageMap = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
    val totalForegroundMs = appUsageMap.values.sumOf { it.getTotalTimeInForeground() }
    val events = usageStatsManager.queryEvents(startTime, endTime)
    var unlocks = 0
    while (events.hasNextEvent()) {
        events.getNextEvent(event)
        if (event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN) unlocks++
    }
    return TodayUsageStats(totalForegroundMs, unlocks)
}
```

### OnboardingViewModel

Antes:
```kotlin
private val _realHours = MutableStateFlow(0)
private val _unlocks = MutableStateFlow(0)
// loadRealUsageStats():
val (hours, unlockCount) = appUsageRepository.getTodayUsageStats()
_realHours.value = hours
_unlocks.value = unlockCount
```

Después:
```kotlin
private val _realUsageStats = MutableStateFlow(TodayUsageStats(0, 0))
val realUsageStats: StateFlow<TodayUsageStats> = _realUsageStats.asStateFlow()
// loadRealUsageStats():
_realUsageStats.value = appUsageRepository.getTodayUsageStats()
```

### RealityRevealScreen

Antes:
```kotlin
fun RealityRevealScreen(realHours: Int = 9, ...)
// display: "${realHours}h"
// TimeCard(hours = realHours)
```

Después:
```kotlin
fun RealityRevealScreen(realUsageStats: TodayUsageStats = TodayUsageStats(0,0), ...)
// display: realUsageStats.formattedTime → "3h 42m"
// TimeCard(hours = realUsageStats.hours)
```

## Interfaces / Contracts

```kotlin
interface UsageStatsDataSource {
    fun queryAndAggregateUsageStats(startTime: Long, endTime: Long): Map<String, UsageStats>
    fun queryEvents(startTime: Long, endTime: Long): UsageEvents
}

class RealUsageStatsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : UsageStatsDataSource {
    private val usm: UsageStatsManager
        get() = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    override fun queryAndAggregateUsageStats(start: Long, end: Long) =
        usm.queryAndAggregateUsageStats(start, end)
    override fun queryEvents(start: Long, end: Long) =
        usm.queryEvents(start, end)
}
```

AppUsageRepository recibe `UsageStatsDataSource` por constructor (reemplaza `context`).

## Testing Strategy

| Capa | Qué testear | Approach |
|---|---|---|
| Unit — DataSource | `RealUsageStatsDataSource` delega al manager real | Fake impl de `UsageStatsDataSource` para pruebas |
| Unit — Repository | `getTodayUsageStats()` con fake data source | Fake retorna mapas controlados + eventos conocidos |
| Unit — ViewModel | `loadRealUsageStats()` propaga `TodayUsageStats` al StateFlow | Mock repository, verificar state emission |
| Unit — Model | `TodayUsageStats.formattedTime`, hours/minutes | Tests sin Android framework, solo Kotlin |

**Test cases** para `AppUsageRepository`:
- Permiso denegado → `TodayUsageStats(0, 0)`
- Sin uso hoy → `TodayUsageStats(0, 0)`
- 30min de uso, 5 unlocks → `TodayUsageStats(1_800_000, 5)` con `formattedTime = "0h 30m"`
- 2h 15min, 0 unlocks → `TodayUsageStats(8_100_000, 0)` (sin inflación)
- Múltiples apps con foreground combinado → suma correcta de `getTotalTimeInForeground()`

## Migration / Rollout

No requiere migración de datos ni feature flags. Los únicos consumidores son `OnboardingViewModel` y `RealityRevealScreen`, ambos en el flujo de onboarding que se ejecuta fresh cada vez. Commit único con todos los cambios.

## Riesgos

| Riesgo | Mitigación |
|---|---|
| `queryAndAggregateUsageStats()` retorna mapa vacío en dispositivos sin datos | `sumOf` sobre mapa vacío da 0 → `TodayUsageStats(0, unlocks)`; UI muestra "0h 0m" |
| Permiso `PACKAGE_USAGE_STATS` denegado → 0 en todo | `hasUsageStatsPermission()` ya está; se muestra 0 y se invita a conceder permiso |
| KEYGUARD_HIDDEN no cuenta desbloqueos en todas las ROMs | Unlock count es secundario en la pantalla; si da 0 no se infla artificialmente |

## Open Questions

- [ ] ¿Usar `UsageStatsDataSource` como interface separada o simplificar y testear con Robolectric directamente? La interface añade indirección pero permite tests unitarios sin framework Android.
