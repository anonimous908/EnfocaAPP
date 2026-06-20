# Proposal: Unify Theme Colors

## Intent

Eliminar la inconsistencia visual provocada por colores hardcodeados en varias pantallas y los colores dinámicos de Android 12+ que dependen del wallpaper. Unificar toda la app bajo un tema formal controlado con modo oscuro y claro, usando los colores hardcodeados actuales como paleta definitiva.

## Scope

### In scope

1. **Desactivar colores dinámicos** en Android 12+ (`dynamicColor = false` por defecto)
2. **Completar DarkColorScheme** con todos los slots de Material3 usando la paleta Enfoca actual
3. **Crear LightColorScheme** formal derivado del mismo azul `#0052FF`, con fondos claros
4. **Refactorizar todas las pantallas** para usar `MaterialTheme.colorScheme.*` en vez de `Color(0xFF...)`
5. **Limpiar Color.kt**: remover colores legacy del template (Purple80, etc.), agregar colores light
6. **Verificar** SplashScreen, WelcomeScreen, InfoScreen, ContractScreen (ya usan theme)

### Out of scope

- Selector de modo claro/oscuro en Ajustes (se hará después)
- Cambios en la palela de colores actual (los oscuros son definitivos)
- Refactor de navegación o arquitectura

## Approach

1. **Color.kt**: Definir `EnfocaLight*` colores para modo claro. Remover `Purple*` muertos.
2. **Theme.kt**: 
   - Cambiar `dynamicColor` default a `false`
   - DarkColorScheme: mapear ~20 slots (primary, onPrimary, primaryContainer, onPrimaryContainer, secondary, background, surface, surfaceVariant, onBackground, onSurface, onSurfaceVariant, outline, outlineVariant, error, etc.)
   - LightColorScheme: usando `EnfocaLight*` colores
3. **Pantallas**: Reemplazar hardcodeos uno por uno:
   - UsageEstimationScreen: ~15 ocurrencias
   - RealityRevealScreen: ~10 ocurrencias
   - MainScreens, StatsScreen: verificar
4. **Verificación**: Build + test visual

## Risks

- **R1**: Si un color mapeado al theme no tiene exactamente el mismo slot Material3 esperado, alguna pantalla puede verse distinta. Mitigación: revisar cada reemplazo contra el color original.
- **R2**: LightColorScheme puede necesitar ajuste fino si algún contraste es insuficiente. Mitigación: arrancar con valores conservadores y ajustar.
- **R3**: Cambiar `dynamicColor` default puede romper la expectativa de usuarios existentes en Android 12+. Mitigación: es un cambio deliberado aprobado por el usuario.

## Files affected

| File | Change type |
|---|---|
| `ui/theme/Color.kt` | Modify |
| `ui/theme/Theme.kt` | Modify |
| `onboarding/UsageEstimationScreen.kt` | Modify |
| `onboarding/RealityRevealScreen.kt` | Modify |
| `main/MainScreens.kt` | Check + possibly modify |
| `main/StatsScreen.kt` (or similar) | Check + possibly modify |

## Non-goals

- No se toca la lógica de negocio
- No se cambia navegación
- No se agrega UI toggle de modo claro
