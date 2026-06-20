# EnfocaAPP2 (EnfocaApp) — Agent Guide

## Identity

Android bienestar digital app (Kotlin + Compose). Internally "EnfocaAPP", marketed as "EnfocaApp". Zero-Cloud — no internet, no servers, encrypted prefs with hardware-backed AES-256.

## One-time setup

```powershell
./gradlew assembleDebug
```

## Commands

| What | Command |
|---|---|
| Unit tests | `./gradlew test` |
| Instrumented tests | `./gradlew connectedCheck` |
| Build debug | `./gradlew assembleDebug` |
| Build release (R8) | `./gradlew assembleRelease` |

## Architecture facts

- **Single Activity** (`MainActivity` + `@AndroidEntryPoint`), one `EnfocaNavGraph` with 2 top-level routes: `onboarding_welcome` → `main`.
- Bottom-nav tabs live in a **nested NavHost** inside `MainScreen` (`Screen.Home`, `Screen.Stats`, `Screen.AppBlock`, `Screen.Settings`).
- «Navegación anidada»: outer `NavHost` (onboarding→main), inner `NavHost` (tabs).
- **Onboarding** = 6-page `HorizontalPager` (Welcome, UsageEstimation, RealityReveal, Info, Rigor, Contract). Contract screen has a **hold-to-sign** `Animatable` that fires on complete.
- Onboarding exit uses `popUpTo(startDestination, inclusive=true)` to destroy backstack.
- DI: Hilt `@InstallIn(SingletonComponent)`. `SecurityModule` provides `EncryptedSharedPreferences`.
- App entry: `EnfocaApplication` (`@HiltAndroidApp`), `EnfocaNavGraph` (`NavHost`).
- Permissions (all runtime): `SYSTEM_ALERT_WINDOW`, `POST_NOTIFICATIONS`, `PACKAGE_USAGE_STATS`, `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`.

## Source layout

Source is at `app/src/main/java/` (NOT `kotlin/` — legacy convention). Test files are `.java` dir too.

```
com.protas.enfocaapp/
├── core/
│   ├── di/SecurityModule.kt          ← EncryptedSharedPreferences provider
│   ├── navigation/Screen.kt          ← sealed class (all routes)
│   ├── navigation/NavGraph.kt        ← EnfocaNavGraph
│   └── repository/
│       ├── AppUsageRepository.kt     ← UsageStatsManager queries
│       └── UserPreferencesRepository.kt ← stores only a token (incomplete)
├── ui/
│   ├── screens/onboarding/           ← 6 screens + OnboardingViewModel
│   ├── screens/main/{MainScreen,StatsScreen,MainScreens}.kt
│   └── theme/
└── {EnfocaApplication,MainActivity}.kt
```

## Known issues (fix or avoid these)

- **OnboardingRigorScreen**: options list missing `ESTRICTO` — only `MODERADO` and `QUIRURGICO`, but default is `ESTRICTO`. Missing card for that default.
- **`NivelRigor`** defined inside `OnboardingRigorScreen.kt` instead of `core/model/`.
- **`UserPreferencesRepository`** saves only a token — does not persist the selected rigor level.
- **Missing DataStore wiring**: `gradle/libs.versions.toml` declares `datastore-preferences` but nothing uses it yet.
- **Stats icon** uses `Icons.Default.List` instead of `Icons.Default.BarChart`.
- **Placeholder screens**: `HomeEmptyScreen`, `AppBlockEmptyScreen`, `SettingsEmptyScreen` — no logic.
- **No tests**: unit + instrumented tests are empty examples only.

## Config & toolchain quirks

- All dependency versions in `gradle/libs.versions.toml` — never hardcode versions in `build.gradle.kts`.
- Build files are **`.kts`** (Kotlin DSL).
- R8 is enabled in release (`isMinifyEnabled`, `isShrinkResources`). Default `proguard-rules.pro` is empty.
- `gradle.properties` has `android.disallowKotlinSourceSets=false`.
- Compiler target: Java 11, Kotlin 2.2.10, AGP 9.2.1, Compile/Target SDK 36, minSdk 29.
- Edge-to-edge enabled via `enableEdgeToEdge()` in `MainActivity`.
- `allowBackup="false"` in manifest — no cloud backup.

## Reference docs (all gitignored, not in repo)

- `DOCUMENTACION_PROYECTO.md` — product/UX specs (Spanish).
- `EXPLICACION_CODIGO.md` — deep technical explanations per file.
- `structure_project.md` — canonical architecture + navigation + known bugs.

## Project infra

- **Mobiai** enabled: `.mobiai/brain/` + `.mobiai/graph/index.json` exist.
- **No CI** (no `.github/`), **no pre-commit hooks**, **no task runner**, **no `opencode.json`**.
- Single module `:app`, single branch `main`.
- Commit messages in Spanish.
