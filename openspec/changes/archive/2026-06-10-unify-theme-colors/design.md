# Design: Unificación de Colores del Tema

## Enfoque Técnico

Eliminar colores hardcodeados `Color(0xFF...)` y colores dinámicos de Android 12+ reemplazándolos por un `DarkColorScheme` completo con la paleta `Enfoca*` existente y un `LightColorScheme` nuevo derivado del azul `#0052FF`. El reemplazo es uno a uno en cada pantalla usando `MaterialTheme.colorScheme.*`.

## Decisiones de Arquitectura

| Opción | Tradeoff | Decisión |
|--------|----------|----------|
| `dynamicColor = false` vs `true` | false: pierde personalización del wallpaper. true: inconsistente con paleta Enfoca. | `false` por defecto |
| LightColorScheme con `EnfocaLight*` vs generar desde MD3 | Control total vs posible desajuste con MD3. | `EnfocaLight*` manual |
| Refactor StatsScreen vs dejarlo con `Enfoca*` | StatsScreen ya usa variables, no `Color(0xFF...)`. | No refactor (spec A4) |

## Cambios en Color.kt

### Eliminar (6 variables legacy)
```
Purple80, PurpleGrey80, Pink80, Purple40, PurpleGrey40, Pink40
```

### Agregar (11 variables light)
```kotlin
val EnfocaLightBackground = Color(0xFFF8F8F8)
val EnfocaLightSurface = Color(0xFFFFFFFF)
val EnfocaLightOnBackground = Color(0xFF1C1B1F)
val EnfocaLightOnSurfaceVariant = Color(0xFF49454F)
val EnfocaLightPrimary = Color(0xFF0052FF)
val EnfocaLightPrimaryContainer = Color(0xFFD6E4FF)
val EnfocaLightSurfaceContainer = Color(0xFFEEE8E0)
val EnfocaLightSurfaceContainerHigh = Color(0xFFE6E0D8)
val EnfocaLightSurfaceContainerLow = Color(0xFFF8F5F0)
val EnfocaLightOutlineVariant = Color(0xFFC4C0BA)
val EnfocaLightOutline = Color(0xFF8A8682)
```

### Mantener (10 variables dark existentes)
```
EnfocaBackground, EnfocaSurfaceContainer, EnfocaPrimaryContainer,
EnfocaOutlineVariant, EnfocaOnBackground, EnfocaOnSurfaceVariant,
EnfocaPrimary, EnfocaSurfaceContainerHigh, EnfocaSurfaceContainerLow,
EnfocaSurfaceContainerHighest
```

## Cambios en Theme.kt

### DarkColorScheme — slots completos

| Slot | Valor |
|------|-------|
| `primary` | `EnfocaPrimary` |
| `onPrimary` | `EnfocaBackground` |
| `primaryContainer` | `EnfocaPrimaryContainer` |
| `onPrimaryContainer` | `EnfocaOnBackground` |
| `secondary` | `EnfocaOnSurfaceVariant` |
| `onSecondary` | `EnfocaBackground` |
| `background` | `EnfocaBackground` |
| `onBackground` | `EnfocaOnBackground` |
| `surface` | `EnfocaBackground` |
| `onSurface` | `EnfocaOnBackground` |
| `surfaceVariant` | `EnfocaSurfaceContainer` |
| `onSurfaceVariant` | `EnfocaOnSurfaceVariant` |
| `outline` | `EnfocaSurfaceContainerHighest` |
| `outlineVariant` | `EnfocaOutlineVariant` |
| `error` | `Color(0xFFFFB4AB)` |
| `onError` | `Color(0xFF690005)` |

### LightColorScheme — slots completos

| Slot | Valor |
|------|-------|
| `primary` | `EnfocaLightPrimary` |
| `onPrimary` | `Color.White` |
| `primaryContainer` | `EnfocaLightPrimaryContainer` |
| `onPrimaryContainer` | `Color(0xFF001B3E)` |
| `secondary` | `EnfocaLightOnSurfaceVariant` |
| `onSecondary` | `Color.White` |
| `background` | `EnfocaLightBackground` |
| `onBackground` | `EnfocaLightOnBackground` |
| `surface` | `EnfocaLightSurface` |
| `onSurface` | `EnfocaLightOnBackground` |
| `surfaceVariant` | `EnfocaLightSurfaceContainer` |
| `onSurfaceVariant` | `EnfocaLightOnSurfaceVariant` |
| `outline` | `EnfocaLightOutline` |
| `outlineVariant` | `EnfocaLightOutlineVariant` |
| `error` | `Color(0xFFBA1A1A)` |
| `onError` | `Color.White` |

### EnfocaAPPTheme — diff funcional

```kotlin
// ANTES
fun EnfocaAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,           // ← activado
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            // ← colores del wallpaper
            ...
        }
        ...
    }
}

// DESPUÉS
fun EnfocaAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,          // ← desactivado
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> { ... }
        darkTheme -> DarkColorScheme        // ← siempre DarkColorScheme completo
        else -> LightColorScheme            // ← LightColorScheme nuevo
    }
    ...
}
```

## Plan de Refactor por Pantalla

### UsageEstimationScreen.kt (primaria — ~25 ocurrencias)

| Contexto | Color actual | Slot target |
|----------|-------------|-------------|
| Fondo Box ppal | `0xFF131313` | `background` |
| Glow radial | `0xFF0052FF` alpha 0.03 | `primaryContainer` alpha 0.03 |
| Título "Seamos honestos..." | `0xFFE5E2E1` | `onBackground` |
| Subtítulo | `0xFFC3C5D9` | `onSurfaceVariant` |
| Card background | `0xFF1C1B1B` | `surfaceContainerLow` |
| Card border | `0xFF353534` | `outline` |
| Número horas | `0xFFE5E2E1` | `onBackground` |
| "h" | `0xFF0052FF` | `primaryContainer` |
| Labels 1h/12h | `0xFFC3C5D9` | `onSurfaceVariant` |
| Feedback severity | 4 colores varios | **EXCEPTUADOS** (semánticos) |
| Botón container | `0xFF0052FF` | `primaryContainer` |
| Botón content | `0xFFDFE3FF` | `onPrimary` |
| Footer muted | `0xFFC3C5D9` alpha 0.7 | `onSurfaceVariant` alpha 0.7 |
| Slider thumb/track | `0xFF0052FF` | `primaryContainer` |
| Sheet container | `0xFF1C1B1B` | `surfaceContainerLow` |
| Sheet handle | `0xFF434656` alpha 0.5 | `outlineVariant` alpha 0.5 |
| Pulse glow | `0xFF0052FF` | `primaryContainer` |
| Icon circle bg | `0xFF2A2A2A` | `surfaceContainerHigh` |
| Icon circle border | `0xFF353534` | `outline` |
| Icon tint | `0xFFB7C4FF` | `primary` |
| Sheet title | `0xFFE5E2E1` | `onBackground` |
| Sheet body text | `0xFFC3C5D9` | `onSurfaceVariant` |
| Sheet muted text | `0xFF8D90A2` | `onSurfaceVariant` alpha 0.8* |
| OutlinedButton container | `0xFF201F1F` | `surfaceVariant` |
| OutlinedButton border | `0xFF353534` | `outline` |
| OutlinedButton content | `0xFFC3C5D9` | `onSurfaceVariant` |

*\* Minor visual diff en dark mode: pasa de `#8D90A2` a `#C3C5D9` alpha 0.8 (~#9C9EAE). Aceptado por ser semánticamente correcto.*

### RealityRevealScreen.kt (secundaria — ~15 ocurrencias)

| Contexto | Color actual | Slot target |
|----------|-------------|-------------|
| Fondo | `0xFF131313` | `background` |
| Subtitle muted | `0xFFC3C5D9` alpha 0.6 | `onSurfaceVariant` alpha 0.6 |
| Texto "pero pasas..." | `0xFFE5E2E1` | `onBackground` |
| Acento azul "9h" | `0xFF0052FF` | `primaryContainer` |
| Card bg | `0xFF2A2A2A` alpha 0.4 | `surfaceContainerHigh` alpha 0.4 |
| Card border | `Color.White` alpha 0.05 | `outline` alpha 0.08 |
| Card label "TIEMPO" | `0xFFC3C5D9` | `onSurfaceVariant` |
| Big number `${hours}h` | `0xFFE5E2E1` | `onBackground` |
| "hoy" label | `0xFFC3C5D9` | `onSurfaceVariant` |
| Bar active | `0xFF0052FF` | `primaryContainer` |
| Bar inactive | `0xFFC3C5D9` alpha 0.2 | `onSurfaceVariant` alpha 0.2 |
| Button container | `0xFFE5E2E1` | `onBackground` |
| Button content | `0xFF131313` | `background` |
| CTA footer text | `0xFFC3C5D9` alpha 0.6 | `onSurfaceVariant` alpha 0.6 |
| Badge bg | `0xFF0052FF` alpha 0.1 | `primaryContainer` alpha 0.1 |
| Badge border | `0xFF0052FF` alpha 0.2 | `primaryContainer` alpha 0.2 |
| Badge text | `0xFF0052FF` | `primaryContainer` |

### SplashScreen.kt — YA usa theme (solo verificar)
`background`, `onSurfaceVariant` — sin cambios.

### OnboardingWelcomeScreen.kt — YA usa theme (solo verificar)
`background`, `primaryContainer`, `onBackground`, `onSurfaceVariant`, `outlineVariant`, `surfaceVariant`, `Color.White` — sin cambios.

### OnboardingInfoScreen.kt — YA usa theme (solo verificar)
`background`, `onSurface`, `onSurfaceVariant`, `primaryContainer`, `Color.White` — sin cambios.

### OnboardingContractScreen.kt — refactor menor (2 ocurrencias)
- `EnfocaSurfaceContainerHighest` → `MaterialTheme.colorScheme.surfaceContainerHighest`
- `EnfocaSurfaceContainerLow` → `MaterialTheme.colorScheme.surfaceContainerLow`

### Screen en main/ — sin hardcodeos
`MainScreens.kt` (solo placeholders), `MainScreen.kt` (Scaffold + NavigationBar) — sin colores directos.

### StatsScreen.kt — sin cambios (usa variables Enfoca*)
Usa `EnfocaBackground`, `EnfocaPrimaryContainer`, `EnfocaOnBackground`, etc. directo. Exentado por spec A4.

## Mapa Completo de Slots

| Slot Material3 | Dark (Enfoca*) | Light (EnfocaLight*) |
|---------------|----------------|----------------------|
| `primary` | `#B7C4FF` | `#0052FF` |
| `onPrimary` | `#131313` | `#FFFFFF` |
| `primaryContainer` | `#0052FF` | `#D6E4FF` |
| `onPrimaryContainer` | `#E5E2E1` | `#001B3E` |
| `secondary` | `#C3C5D9` | `#49454F` |
| `onSecondary` | `#131313` | `#FFFFFF` |
| `background` | `#131313` | `#F8F8F8` |
| `onBackground` | `#E5E2E1` | `#1C1B1F` |
| `surface` | `#131313` | `#FFFFFF` |
| `onSurface` | `#E5E2E1` | `#1C1B1F` |
| `surfaceVariant` | `#201F1F` | `#EEE8E0` |
| `onSurfaceVariant` | `#C3C5D9` | `#49454F` |
| `outline` | `#353534` | `#8A8682` |
| `outlineVariant` | `#434656` | `#C4C0BA` |
| `error` | `#FFB4AB` | `#BA1A1A` |
| `onError` | `#690005` | `#FFFFFF` |

## Build y Verificación

| Paso | Comando |
|------|---------|
| Build debug | `./gradlew assembleDebug` |
| Tests unitarios | `./gradlew test` |
| Verificación visual | Renderizar splash + onboarding completo + stats en modo oscuro y comparar pixel a pixel con versión anterior |

## Riesgos y Mitigaciones

| Riesgo | Impacto | Mitigación |
|--------|---------|------------|
| R1: `surfaceContainerLow/High` no existe en Material3 de la BOM actual | Compilation error | Verificar BOM version; si no existe, mapear a `surfaceVariant` o `surface` |
| R2: `Color.White.copy(alpha = 0.05f)` → `outline.copy(alpha = 0.08)` cambia aspectо en RealityRevealScreen | Visual diff sutil | Documentado; aceptado porque el borde es casi invisible |
| R3: `0xFF8D90A2` → `onSurfaceVariant` cambia color de texto muted | Visual diff: texto más claro | Aceptado por consistencia semántica del theme |
| R4: LightColorScheme sin probar visualmente | Posible contraste insuficiente | Ajustar valores si es necesario; el toggle no está en producción aún |

## Archivos Modificados

| Archivo | Acción |
|---------|--------|
| `ui/theme/Color.kt` | Modificar |
| `ui/theme/Theme.kt` | Modificar |
| `onboarding/UsageEstimationScreen.kt` | Modificar |
| `onboarding/RealityRevealScreen.kt` | Modificar |
| `onboarding/OnboardingContractScreen.kt` | Modificar (menor) |

## Preguntas Abiertas

- [ ] Verificar que `surfaceContainerLow`, `surfaceContainerHigh` existen en la versión de Material3 declarada en `libs.versions.toml`
- [ ] Confirmar que `0xFFDFE3FF` como `onPrimary` es aceptable visualmente (actualmente hardcodeado en botones)
