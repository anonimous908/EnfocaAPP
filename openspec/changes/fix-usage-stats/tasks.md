# Tasks: fix-usage-stats — Precisión de datos de uso

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~230 |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | ask-on-risk |
| Chain strategy | pending |

Decision needed before apply: Yes
Chained PRs recommended: No
Chain strategy: pending
400-line budget risk: Low

## Phase 1: Modelo e Infraestructura

- [x] 1.1 Crear `core/model/TodayUsageStats.kt` — `data class` con `totalForegroundMs: Long`, `unlockCount: Int`, computed `hours`/`minutes`/`formattedTime`
- [x] 1.2 Crear `core/repository/UsageStatsDataSource.kt` — interface + `RealUsageStatsDataSource` que delega a `UsageStatsManager.queryAndAggregateUsageStats()` y `queryEvents()`
- [x] 1.3 Agregar `@Provides @Singleton` de `RealUsageStatsDataSource` en `core/di/SecurityModule.kt`

## Phase 2: Implementación Core

- [x] 2.1 Modificar `AppUsageRepository.kt` — inyectar `UsageStatsDataSource` vía constructor, reescribir `getTodayUsageStats()` con `queryAndAggregateUsageStats` (sum `getTotalTimeInForeground`) + `KEYGUARD_HIDDEN` (unlocks), eliminar inflación de 1h/1 unlock, retornar `TodayUsageStats`
- [x] 2.2 Modificar `OnboardingViewModel.kt` — migrar de `_realHours: StateFlow<Int>` + `_unlocks: StateFlow<Int>` a `_realUsageStats: StateFlow<TodayUsageStats>`, propagar desde `getTodayUsageStats()`
- [x] 2.3 Modificar `RealityRevealScreen.kt` — reemplazar `realHours: Int`/`unlocks: Int` por `realUsageStats: TodayUsageStats`; mostrar `formattedTime` ("Xh Ym") en TimeCard y texto cabecera

## Phase 3: Tests

- [x] 3.1 Crear `FakeUsageStatsDataSource` en `test/` — retorna valores controlados para cubrir SCE-01 a SCE-09
- [x] 3.2 Test unitario `TodayUsageStatsTest` — verificar `hours`/`minutes`/`formattedTime` para 0ms, 2.700.000ms (45min), 9.000.000ms (2h30m)
- [x] 3.3 Test unitario `AppUsageRepositoryTest` — fake data source, cubrir permiso denegado (SCE-09), 0 uso (SCE-01/04), 45min sin redondeo (SCE-03), 2h30m exacto (SCE-02), 15 unlocks (SCE-05), 0 unlocks sin inflación (SCE-06), mapa vacío (SCE-04)
- [x] 3.4 Test unitario `OnboardingViewModelTest` — mock repository, verificar que `loadRealUsageStats()` emite `TodayUsageStats` correcto al StateFlow

## Phase 4: Verificación

- [x] 4.1 Ejecutar `./gradlew test` y confirmar que todos los tests pasan (SCE-10)
