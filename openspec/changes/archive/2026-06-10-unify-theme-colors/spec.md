# Spec: Unificación de Colores del Tema

## Requirements

### REQ-01: Colores dinámicos desactivados

`EnfocaAPPTheme` DEBE usar `dynamicColor = false` por defecto. El sistema NO DEBE leer el wallpaper en Android 12+.

#### Escenario: Wallpaper naranja en Android 14

- GIVEN un dispositivo Android 14 con wallpaper naranja
- WHEN `EnfocaAPPTheme` se compone sin pasar `dynamicColor`
- THEN `colorScheme` DEBE ser `DarkColorScheme` o `LightColorScheme` según el sistema
- AND los acentos DEBEN ser azul `#0052FF`, NO naranja

### REQ-02: DarkColorScheme completo

`DarkColorScheme` DEBE mapear TODOS los slots Material3 usados: `primary`, `onPrimary`, `primaryContainer`, `onPrimaryContainer`, `secondary`, `onSecondary`, `background`, `onBackground`, `surface`, `onSurface`, `surfaceVariant`, `onSurfaceVariant`, `outline`, `outlineVariant`, `error`, `onError`.

#### Escenario: Slots completos

- GIVEN `DarkColorScheme` definido en Theme.kt
- WHEN se inspeccionan los slots del scheme
- THEN cada slot DEBE apuntar a una variable `Enfoca*` de Color.kt
- AND `onPrimary`, `onSecondary`, `onSurface`, `outline`, `error`, `onError` DEBEN estar definidos (no usar defaults)

### REQ-03: LightColorScheme derivado del azul

`LightColorScheme` DEBE usar colores `EnfocaLight*` derivados del mismo azul `#0052FF`, con fondos claros y texto oscuro.

#### Escenario: Esquema claro funcional

- GIVEN el sistema en modo claro
- WHEN `EnfocaAPPTheme` se compone
- THEN `LightColorScheme` DEBE activarse
- AND el fondo DEBE ser claro (ej. `#FFF8F5`), no oscuro

### REQ-04: Sin colores hardcodeados en pantallas

NINGUNA pantalla DEBE usar `Color(0xFF...)` directo para colores de tema. Todo DEBE pasar por `MaterialTheme.colorScheme.*`.

Quedan EXCEPTUADOS: colores semánticos de estado (ej. feedback severity: rojo para "Matrix", gris para "Monje").

#### Escenario: UsageEstimationScreen sin hardcodeos

- GIVEN `UsageEstimationScreen.kt`
- WHEN se revisan los `Color(0xFF...)` en background, text, border, button
- THEN CADA UNO DEBE reemplazarse por `MaterialTheme.colorScheme.*`
- AND el resultado visual DEBE ser idéntico en modo oscuro

#### Escenario: RealityRevealScreen sin hardcodeos

- GIVEN `RealityRevealScreen.kt`
- WHEN se revisan `Color(0xFF...)` en cards, textos, botones, badges
- THEN DEBEN reemplazarse por `MaterialTheme.colorScheme.*`
- AND el resultado visual DEBE ser idéntico en modo oscuro

### REQ-05: Consistencia visual entre pantallas

TODAS las pantallas DEBEN usar el mismo `background` en el mismo modo. No DEBE haber diferencias de fondo entre onboarding, stats, home, etc.

#### Escenario: Mismo fondo en onboarding y stats

- GIVEN modo oscuro activo
- WHEN se navega de `UsageEstimationScreen` a `StatsScreen`
- THEN AMBAS DEBEN tener `background = MaterialTheme.colorScheme.background`
- AND el valor DEBE ser el mismo `Color(0xFF131313)`

### REQ-06: Color.kt limpio

`Color.kt` DEBE eliminar `Purple80`, `PurpleGrey80`, `Pink80`, `Purple40`, `PurpleGrey40`, `Pink40`. DEBE agregar variables `EnfocaLight*` para modo claro.

#### Escenario: Sin colores legacy

- GIVEN `Color.kt`
- WHEN se compila el proyecto
- THEN NO DEBE haber referencias a `Purple*` o `Pink*` en ningún archivo fuente

### REQ-07: Identidad oscura preservada

Los valores oscuros actuales (`#131313`, `#0052FF`, `#E5E2E1`, etc.) NO DEBEN cambiar. Son la paleta definitiva.

#### Escenario: Mapa uno a uno de colores oscuros

- GIVEN los colores Enfoca* actuales en Color.kt
- WHEN se mapean a DarkColorScheme
- THEN `EnfocaBackground` → `background` DEBE ser `0xFF131313`
- AND `EnfocaPrimaryContainer` → `primaryContainer` DEBE ser `0xFF0052FF`
- AND CADA valor DEBE coincidir exactamente con el hardcodeo que reemplaza

## Escenarios de verificación

### SCE-01: Wallpaper ignorado en Android 12+

- GIVEN un dispositivo Android 12+ con wallpaper naranja
- WHEN la app se abre sin configuración de tema
- THEN todos los acentos DEBEN ser azules
- AND NO DEBE haber naranja en la UI

### SCE-02: Toggle sistema claro/oscuro

- GIVEN la app abierta
- WHEN el usuario cambia entre modo claro y oscuro desde ajustes del sistema
- THEN `EnfocaAPPTheme` DEBE cambiar entre `DarkColorScheme` y `LightColorScheme`
- AND todos los textos DEBEN ser legibles en ambos modos

### SCE-03: Onboarding completo con fondo consistente

- GIVEN el flujo de onboarding (6 pantallas)
- WHEN el usuario navega secuencialmente
- THEN el fondo DEBE ser el mismo en pantallas que usan `MaterialTheme.colorScheme.background`

### SCE-04: Build exitoso

- GIVEN todos los archivos modificados
- WHEN `./gradlew assembleDebug` se ejecuta
- THEN el build DEBE pasar sin errores de compilación

### SCE-05: Sin regresiones visuales en modo oscuro

- GIVEN modo oscuro activo y splash + onboarding + stats renderizados
- WHEN se compara pixel a pixel con la versión anterior
- THEN NO DEBE haber diferencias visuales (los colores oscuros son idénticos)

## Constraints

| # | Constraint |
|---|-----------|
| C1 | Usar las variables `Enfoca*` existentes en `Color.kt` para el esquema oscuro |
| C2 | Los colores claros DEBEN agregarse como `EnfocaLight*` en `Color.kt` |
| C3 | `dynamicColor` DEBE default a `false` en `EnfocaAPPTheme` |
| C4 | `DarkColorScheme` DEBE coincidir exactamente con los valores hardcodeados actuales |

## Assumptions

| # | Assumption |
|---|-----------|
| A1 | Los colores oscuros hardcodeados actuales son la paleta definitiva aprobada por el usuario |
| A2 | La paleta clara se deriva conservadoramente del mismo azul `#0052FF` |
| A3 | El toggle de modo claro/oscuro en Ajustes vendrá en un cambio futuro |
| A4 | `StatsScreen.kt` ya usa variables `Enfoca*` — no necesita refactor |
