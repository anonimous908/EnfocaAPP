# Usage Stats

## Purpose

Daily usage statistics (foreground time + unlocks) using `UsageStatsManager.queryAndAggregateUsageStats()` for accurate foreground measurement and `queryEvents()` with `KEYGUARD_HIDDEN` for unlock counting. No artificial inflation.

## Requirements

### REQ-01: Medición con `queryAndAggregateUsageStats()`

| Campo | Valor |
|-------|-------|
| **Descripción** | `getTodayUsageStats()` MUST usar `queryAndAggregateUsageStats()` y sumar `getTotalTimeInForeground()` de todos los paquetes para el tiempo real en primer plano. |
| **RFC 2119** | MUST |

#### SCE-01: 0 uso — 0h 0m, 0 unlocks
- GIVEN no hay actividad de apps registrada hoy
- WHEN `getTodayUsageStats()`
- THEN `TodayUsageStats(0, 0, 0)`

#### SCE-02: 2h 30m de uso real
- GIVEN `queryAndAggregateUsageStats()` suma 9.000.000 ms foreground
- WHEN `getTodayUsageStats()`
- THEN `TodayUsageStats(2, 30, n)`

#### SCE-03: 45 min — NO redondea a 1h
- GIVEN suma 2.700.000 ms foreground
- WHEN `getTodayUsageStats()`
- THEN `TodayUsageStats(0, 45, n)` y NO `(1, 0, n)`

#### SCE-04: Mapa vacío
- GIVEN `queryAndAggregateUsageStats()` retorna `{}`
- WHEN `getTodayUsageStats()`
- THEN `TodayUsageStats(0, 0, 0)`

### REQ-02: Desbloqueos solo con `KEYGUARD_HIDDEN`

| Campo | Valor |
|-------|-------|
| **Descripción** | MUST conservar `queryEvents()` EXCLUSIVAMENTE para eventos `KEYGUARD_HIDDEN`. NO usar `SCREEN_INTERACTIVE`/`SCREEN_NON_INTERACTIVE`. |
| **RFC 2119** | MUST |

#### SCE-05: 15 desbloqueos reales
- GIVEN `queryEvents()` registra 15 eventos `KEYGUARD_HIDDEN` hoy
- WHEN `getTodayUsageStats()`
- THEN `unlockCount = 15`

#### SCE-06: 0 desbloqueos (sin inflación)
- GIVEN `queryEvents()` registra 0 eventos `KEYGUARD_HIDDEN`
- WHEN `getTodayUsageStats()`
- THEN `unlockCount = 0` (no 1)

### REQ-03: Sin inflación artificial

| Campo | Valor |
|-------|-------|
| **Descripción** | MUST NOT redondear <1h a 1h. MUST NOT forzar mínimo 1 unlock. Los valores reflejan datos exactos del sistema. |
| **RFC 2119** | MUST NOT |

### REQ-04: Precisión en minutos

| Campo | Valor |
|-------|-------|
| **Descripción** | MUST descomponer tiempo en horas Y minutos. NO solo horas enteras. `minutes` MUST estar en rango 0-59. |
| **RFC 2119** | MUST |

### REQ-05: `TodayUsageStats` data class

| Campo | Valor |
|-------|-------|
| **Descripción** | Debe existir un `data class TodayUsageStats` con `hours: Int`, `minutes: Int`, `unlockCount: Int`. ViewModel MUST migrar de `Pair<Int,Int>` a este tipo. |
| **RFC 2119** | MUST |

### REQ-06: Formato "Xh Ym" en RealityRevealScreen

| Campo | Valor |
|-------|-------|
| **Descripción** | `TimeCard` MUST mostrar "Xh Ym" (ej. "2h 30m", "0h 45m"). Debe aceptar `hours` y `minutes` por separado. |
| **RFC 2119** | MUST |

#### SCE-07: Pantalla con 0h 0m
- GIVEN `getTodayUsageStats()` retorna `(0, 0, 0)`
- WHEN `RealityRevealScreen` se renderiza
- THEN muestra "0h 0m" y "0" desbloqueos

#### SCE-08: Pantalla con 2h 30m
- GIVEN `getTodayUsageStats()` retorna `(2, 30, 15)`
- WHEN `RealityRevealScreen` se renderiza
- THEN muestra "2h 30m" y "15" desbloqueos

### REQ-07: Permiso denegado → ceros

| Campo | Valor |
|-------|-------|
| **Descripción** | Si `hasUsageStatsPermission()` es `false`, MUST retornar `TodayUsageStats(0, 0, 0)` sin invocar APIs de UsageStatsManager. |
| **RFC 2119** | MUST |

#### SCE-09: Permiso denegado
- GIVEN `hasUsageStatsPermission()` retorna `false`
- WHEN `getTodayUsageStats()`
- THEN `TodayUsageStats(0, 0, 0)`

### REQ-08: Tests unitarios

| Campo | Valor |
|-------|-------|
| **Descripción** | SHOULD existir tests para `getTodayUsageStats()` cubriendo SCE-01 a SCE-09. `./gradlew test` MUST pasar. |
| **RFC 2119** | SHOULD / MUST |

#### SCE-10: Build y tests verdes
- GIVEN el código compila sin errores
- WHEN `./gradlew test`
- THEN todos los tests pasan
