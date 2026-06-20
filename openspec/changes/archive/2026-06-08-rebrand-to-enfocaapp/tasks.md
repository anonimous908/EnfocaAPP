# Tasks: Rebrand to EnfocaApp

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: pending
400-line budget risk: Low

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~120 |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | ask-on-risk |

### Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Full rebrand (rename + UI + icon) | PR 1 | Single PR — all workstreams are independent, parallel-safe. |

## Workstream A — Rename & Refactor (parallel agent)

- [x] **A.1**: Update `strings.xml` — change `app_name` to "EnfocaApp", `onboarding_info_descripcion` to "EnfocaApp" (fix "Dopamine" typo), `onboarding_contract_text_2` to "EnfocaApp"
  Files: `app/src/main/res/values/strings.xml` — 3 string values
- [x] **A.2**: Rename 10 color vars `Dopamina*` → `Enfoca*` in `Color.kt`
  Files: `app/src/main/java/.../ui/theme/Color.kt` — comment + 10 val declarations
- [x] **A.3**: Update 8 references in `Theme.kt` to use renamed `Enfoca*` variables
  Files: `app/src/main/java/.../ui/theme/Theme.kt` — DarkColorScheme assignments
- [x] **A.4**: Replace 2 hardcoded "Dopamina Guard" strings in privacy/terms dialogs + update 4 color variable references in `OnboardingContractScreen.kt`
  Files: `app/src/main/java/.../onboarding/OnboardingContractScreen.kt`
- [x] **A.5**: Update 2 unused imports (`DopaminaSurfaceContainerHigh`, `DopaminaSurfaceContainerLow`) in `OnboardingInfoScreen.kt`
  Files: `app/src/main/java/.../onboarding/OnboardingInfoScreen.kt`
- [x] **A.6**: Replace 1 hardcoded "Dopamina Guard" string (line 66) + update 16 color refs in `StatsScreen.kt`
  Files: `app/src/main/java/.../main/StatsScreen.kt`
- [x] **A.7**: Update `AGENTS.md` identity lines 1 and 5 — "Dopamina Guard" → "EnfocaApp"
  Files: `AGENTS.md`

## Workstream B — Welcome Screen UI (parallel agent)

- [x] **B.1**: Increase logo `Modifier.size` from 64dp to 128dp in `OnboardingWelcomeScreen.kt`
  Files: `app/src/main/java/.../onboarding/OnboardingWelcomeScreen.kt`
- [x] **B.2**: Increase hub Box size from 96dp to 128dp to accommodate larger logo
  Files: `OnboardingWelcomeScreen.kt`
- [x] **B.3**: Increase pulsing ring base size from 64dp to 160dp for proper visual hierarchy
  Files: `OnboardingWelcomeScreen.kt`
- [x] **B.4**: Replace `ScanningOverlay()` composable with new `ScannerRing` composable: rotating 120° arc at 144dp via `Canvas` + `drawArc` + `rememberInfiniteTransition`, `Brush.sweepGradient`, `tween(3000ms, LinearEasing)`, `StrokeCap.Round`, 3dp stroke
  Files: `OnboardingWelcomeScreen.kt`
- [x] **B.5**: Wrap description `Text` in `Surface(shape = RoundedCornerShape(12.dp), color = surfaceVariant.copy(alpha = 0.5f))` with `Modifier.padding(top = 8.dp, bottom = 24.dp)`
  Files: `OnboardingWelcomeScreen.kt`

## Workstream C — App Icon (parallel agent)

- [x] **C.1**: Replace `ic_launcher_foreground.xml` vector paths with `<inset drawable="@drawable/logo" android:inset="25%" />`
  Files: `app/src/main/res/drawable/ic_launcher_foreground.xml`
- [x] **C.2**: Replace `ic_launcher_background.xml` green grid paths with solid `<path android:fillColor="#FF131313" android:pathData="M0,0h108v108h-108z" />`
  Files: `app/src/main/res/drawable/ic_launcher_background.xml`

## Workstream D — Verification

- [x] **D.1**: Run `./gradlew assembleDebug` — verify build succeeds with zero errors
- [x] **D.2**: Grep all `.kt` and `.xml` files — verify zero remaining "Dopamina" or "Dopamine" references
- [ ] **D.3**: Emulator smoke-test: welcome screen renders logo at 128dp, scanner ring animates, description is in styled card
- [ ] **D.4**: Emulator launcher check: app icon shows custom logo (not Android robot) with `#131313` background
